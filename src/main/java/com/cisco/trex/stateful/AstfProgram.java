package com.cisco.trex.stateful;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Emulation L7 program
 *
 * @<code> ASTFProgram progServer =new ASTFProgram()
 * progServer.recv(len(http_req))
 * progServer.send(http_response)
 * progServer.delay(10)
 * progServer.reset()
 * </code>
 */
public class AstfProgram {

    private static final int MIN_DELAY = 50;
    private static final int MAX_DELAY = 700000;
    private static final int MAX_KEEPALIVE = 500000;
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String COMMANDS = "commands";

    private Map<String, Integer> vars = new HashMap();
    private Map<String, Integer> labels = new HashMap();
    private Map<String, List<AstfCmd>> fields = new HashMap();
    private int totalSendBytes = 0;
    private int totalRcvBytes = 0;

    private File file;
    private SideType side;
    private List<AstfCmd> commands;
    private boolean stream = true;
    private static BufferList bufList = new BufferList();

    /**
     * default construct
     */
    public AstfProgram() {
        this(SideType.Client);
    }

    /**
     * construct
     *
     * @param side
     */
    public AstfProgram(SideType side) {
        this(null, side, null, true);
    }

    /**
     * Construct
     *
     * @param file     pcap file
     * @param side     server side or client side
     * @param commands
     * @param stream
     */
    public AstfProgram(File file, SideType side, List<AstfCmd> commands, boolean stream) {
        this.file = file;
        this.side = side;
        this.commands = commands;
        this.stream = stream;
        fields.put(COMMANDS, new ArrayList<AstfCmd>());
        if (file != null) {
            /**
             * TODO: pcap secnario,need to be done in the future.
             */
        } else if (commands != null) {
            setCmds(commands);
        }
    }

    private void setCmds(List<AstfCmd> commands) {
        for (AstfCmd cmd : commands) {
            if (cmd.isBuffer()) {
                if (cmd instanceof AstfCmdTxPkt) {
                    AstfCmdTxPkt txPktCmd = (AstfCmdTxPkt) cmd;
                    totalSendBytes += txPktCmd.getBufLen();
                    txPktCmd.setbufIndex(AstfProgram.bufList.add(txPktCmd.buf()));
                }
                if (cmd instanceof AstfCmdSend) {
                    AstfCmdSend sendCmd = (AstfCmdSend) cmd;
                    totalSendBytes += sendCmd.getBufLen();
                    sendCmd.setbufIndex(AstfProgram.bufList.add(sendCmd.buf()));
                }
            }
            fields.get(COMMANDS).add(cmd);
        }
    }

    /**
     * in case of pcap file need to copy the keepalive command from client to server side
     *
     * @param progS AstfProgram server
     */
    public void updateKeepAlive(AstfProgram progS) {
        if (fields.get(COMMANDS).size() > 0) {
            AstfCmd cmd = fields.get(COMMANDS).get(0);
            if (cmd instanceof AstfCmdKeepaliveMsg) {
                progS.fields.get(COMMANDS).add(0, cmd);
            }
        }
    }

    /**
     * delay for a random time betwean  min-max usec with uniform distribution
     *
     * @param minUsec
     * @param maxUsec
     */
    public void delayRand(int minUsec, int maxUsec) {
        if (minUsec > maxUsec) {
            throw new IllegalStateException(String.format("minUsec %d is bigger than maxUsec %d", minUsec, maxUsec));
        }
        fields.get(COMMANDS).add(new AstfCmdDelayRnd(minUsec, maxUsec));
    }

    /**
     * delay cmd
     *
     * @param usec delay seconds
     */
    public void delay(int usec) {
        if (usec < 0) {
            throw new IllegalStateException(String.format("usec %d is less than 0", usec));
        }
        fields.get(COMMANDS).add(new AstfCmdDelay(usec));
    }

    /**
     * send (l7_buffer) over TCP and wait for the buffer to be acked by peer. Rx side could work in parallel
     * <p>
     * example1
     * send (buffer1)
     * send (buffer2)
     * <p>
     * Will behave differently than
     * <p>
     * example1
     * send (buffer1+ buffer2)
     * <p>
     * in the first example there would be PUSH in the last byte of the buffer and immediate ACK from peer while in the last example the buffer will be sent together (might be one segment)
     *
     * @param buf l7 stream as string
     */
    public void send(String buf) {
        //we support bytes or ascii strings
        AstfCmdSend cmd = null;
        try {
            cmd = new AstfCmdSend(buf.getBytes("ascii"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported Encoding Exception", e);
        }
        this.totalSendBytes += cmd.getBufLen();

        cmd.setbufIndex(bufList.add(cmd.buf()));
        fields.get(COMMANDS).add(cmd);
    }

    /**
     * send UDP message
     *
     * @param buf l7 stream as string
     */
    public void sendMsg(String buf) {
        AstfCmdTxPkt cmd = null;
        try {
            cmd = new AstfCmdTxPkt(buf.getBytes("ascii"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported Encoding Exception", e);
        }
        this.totalSendBytes += cmd.getBufLen();
        cmd.setbufIndex(bufList.add(cmd.buf()));
        fields.get(COMMANDS).add(cmd);
    }

    /**
     * Send l7_buffer by splitting it into small chunks and issue a delay betwean each chunk.
     * This is a utility  command that works on top of send/delay command
     * <p>
     * example1:
     * send (buffer1,100,10) will split the buffer to buffers of 100 bytes with delay of 10usec
     *
     * @param l7Buf     l7 stream as string
     * @param chunkSize size of each chunk
     * @param delayUsec the delay in usec to insert betwean each write
     */
    public void sendChunk(String l7Buf, int chunkSize, int delayUsec) {
        int size = l7Buf.length();
        int cnt = 0;
        while (size > 0) {
            if (cnt + chunkSize < size) {
                this.send(l7Buf.substring(cnt, cnt + chunkSize));
            } else {
                this.send(l7Buf.substring(cnt, l7Buf.length()));
            }

            if (delayUsec > 0) {
                this.delay(delayUsec);
            }
            cnt += chunkSize;
            size -= chunkSize;
        }
    }

    /**
     * recv bytes command
     *
     * @param bytes
     */
    public void recv(int bytes) {
        recv(bytes, false);
    }

    /**
     * recv bytes command
     *
     * @param bytes
     * @param clear
     */
    public void recv(int bytes, boolean clear) {
        this.totalRcvBytes += bytes;
        fields.get(COMMANDS).add(new AstfCmdRecv(totalRcvBytes, clear));
    }

    /**
     * recv msg, works for UDP flow
     *
     * @param pkts wait until the rx packet watermark is reached on flow counter.
     */
    public void recvMsg(int pkts) {
        recvMsg(pkts, false);
    }

    /**
     * recv Msg cmd
     *
     * @param pkts  wait until the rx packet watermark is reached on flow counter.
     * @param clear when reach the watermark clear the flow counter
     */
    public void recvMsg(int pkts, boolean clear) {
        this.totalRcvBytes += pkts;
        fields.get(COMMANDS).add(new AstfCmdRecvMsg(this.totalRcvBytes, clear));
    }

    /**
     * For TCP connection send RST to peer. Should be the last command
     */
    public void reset() {
        fields.get(COMMANDS).add(new AstfCmdReset());
    }

    /**
     * For TCP connection wait for peer side to close (read==0) and only then close. Should be the last command
     * This simulates server side that waits for a requests until client retire with close().
     */
    public void waitForPeerClose() {
        fields.get(COMMANDS).add(new AstfCmdNoClose());
    }

    /**
     * for TCP connection wait for the connection to be connected. should be the first command in the client side
     */
    public void connect() {
        fields.get(COMMANDS).add(new AstfCmdConnect());
    }


    /**
     * close msg,explicit UDP flow close
     */
    public void closeMsg() {
        this.fields.get(COMMANDS).add(new AstfCmdCloseMsg());
    }

    /**
     * set_send_blocking (block), set the stream transmit mode
     * block : for send command wait until the last byte is ack
     * non-block: continue to the next command when the queue is almost empty, this is good for pipeline the transmit
     *
     * @param block
     */
    public void setSendBlocking(boolean block) {
        int flags = block ? 0 : 1;
        this.fields.get(COMMANDS).add(new AstfCmdTxMode(flags));
    }

    public void setKeepAliveMsg(int msec) {
        this.fields.get(COMMANDS).add(new AstfCmdRecvMsg(this.totalRcvBytes, false));
    }

    /**
     * set var command
     *
     * @param varId
     * @param value
     */
    public void setVar(String varId, int value) {
        addVar(varId);
        fields.get(COMMANDS).add(new AstfCmdSetVal(varId, value));
    }

    /**
     * Set a location label name. used with jmp_nz command
     *
     * @param label
     */
    public void setLabel(String label) {
        if (labels.containsKey(label)) {
            throw new IllegalStateException(String.format("label %s was defined already", label));
        }
        labels.put(label, fields.get(COMMANDS).size());
    }

    /**
     * Decrement the flow variable, in case of none zero jump to label
     *
     * @param varId flow var id
     * @param label label id
     */
    public void jmpNz(String varId, String label) {
        fields.get(COMMANDS).add(new AstfCmdJmpnz(varId, 0, label));
    }

    /**
     * get the total send bytes of the program
     *
     * @return
     */
    public int getTotalSendBytes() {
        return totalSendBytes;
    }

    /**
     * return true if it's stream
     *
     * @return
     */
    public boolean isStream() {
        return stream;
    }

    /**
     * including all cached astf programs json format
     *
     * @return json string
     */
    public static JsonArray classToJson() {
        return bufList.toJson();
    }

    /**
     * class reset, clear all cached buffer
     */
    public static void classReset() {
        bufList = new BufferList();
    }

    /**
     * get buffer list size
     *
     * @return
     */
    public static int getBufSize() {
        return bufList.getLen();
    }

    /**
     * to json format
     *
     * @return json string
     */
    public JsonObject toJson() {
        compile();
        JsonObject jsonObject = new JsonObject();
        if (!this.stream) {
            jsonObject.addProperty("stream", false);
        }
        JsonArray jsonArray = new JsonArray();
        for (AstfCmd cmd : fields.get(COMMANDS)) {
            jsonArray.add(cmd.toJson());
        }
        jsonObject.add(COMMANDS, jsonArray);
        return jsonObject;
    }

    private void addVar(String varName) {
        if (!vars.containsKey(varName)) {
            int varIndex = vars.size();
            vars.put(varName, varIndex);
        }
    }

    private int getVarIndex(String varName) {
        if (vars.containsKey(varName)) {
            return vars.get(varName);
        }
        throw new IllegalStateException(String.format("varName %s is not existed", varName));
    }

    private int getLabelId(String label) {
        if (labels.containsKey(label)) {
            return labels.get(label);
        }
        throw new IllegalStateException(String.format("label %s is not existed", label));
    }

    /**
     * update offsets for  AstfCmdJmpnz
     * comvert var names to ids
     */
    private void compile() {
        int i = 0;
        for (AstfCmd cmd : fields.get(COMMANDS)) {
            if (cmd.isStream() && !this.stream) {
                throw new IllegalStateException(String.format(" Command %s stream mode is %s and different from the flow stream mode %s", cmd.getName(), cmd.isStream(), this.stream));
            }
            if (cmd instanceof AstfCmdJmpnz) {
                AstfCmdJmpnz cmdJmpnz = (AstfCmdJmpnz) cmd;
                cmdJmpnz.fields.addProperty("offset", getLabelId(cmdJmpnz.getLabel()) - i);
                cmdJmpnz.fields.addProperty("id", getVarIndex(cmdJmpnz.fields.get("id").getAsString()));
            }
            if (cmd instanceof AstfCmdSetVal) {
                cmd.fields.addProperty("id", getVarIndex(cmd.fields.get("id").getAsString()));
            }
            i++;
        }
    }

    /**
     * cached Buffer class for inner use
     */
    static class BufferList {
        List<String> bufList = new ArrayList<>();
        Map<String, Integer> bufHash = new HashMap<>();

        /**
         * get buf list length
         *
         * @return buf list length
         */
        public int getLen() {
            return bufList.size();
        }

        /**
         * add buf to bufList
         *
         * @param base64Buf should be base64 encode string
         * @return the index of the raw buf.
         */
        public int add(String base64Buf) {
            String sha256Buf = encodeSha256(base64Buf);
            if (bufHash.containsKey(sha256Buf)) {
                return bufHash.get(sha256Buf);
            } else {
                bufList.add(base64Buf);
                int newIndex = bufList.size() - 1;
                bufHash.put(sha256Buf, newIndex);
                return newIndex;
            }
        }

        /**
         * to json format
         *
         * @return json string
         */
        public JsonArray toJson() {
            JsonArray jsonArray = new JsonArray();
            for (String buf : bufList) {
                jsonArray.add(buf);
            }
            return jsonArray;
        }

    }

    /**
     * @param buf should be base64 encode string
     * @return Hex string of the sha256 encode buf
     */
    private static String encodeSha256(String buf) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = sha256.digest(buf.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not generate MD5", e);
        }
    }

    /**
     * Side type
     */
    public enum SideType {
        Client("client"),
        Server("server");

        String type;

        /**
         * Construct
         *
         * @param type
         */
        SideType(String type) {
            this.type = type;
        }

        /**
         * getType
         *
         * @return type
         */
        public String getType() {
            return type;
        }}

}

