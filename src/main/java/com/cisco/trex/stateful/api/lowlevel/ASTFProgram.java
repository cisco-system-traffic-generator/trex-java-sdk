package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
 * Java implementation for TRex python sdk ASTFProgram class
 *
 * <p>Emulation L7 program <code> ASTFProgram progServer =new ASTFProgram()
 * progServer.recv(http_req.length())
 * progServer.send(http_response)
 * progServer.delay(10)
 * progServer.reset()
 * </code>
 */
public class ASTFProgram {
  private static final int MIN_DELAY = 50;
  private static final int MAX_DELAY = 700000;
  private static final int MAX_KEEPALIVE = 500000;
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  private static final String COMMANDS = "commands";

  private Map<String, Integer> vars = new HashMap();
  private Map<String, Integer> labels = new HashMap();
  private Map<String, List<ASTFCmd>> fields = new HashMap();
  private int totalSendBytes = 0;
  private int totalRcvBytes = 0;
  private int payloadLen = 0;

  private String filePath;
  private SideType side;
  private List<ASTFCmd> commands;
  private boolean stream = true;
  private static BufferList bufList = new BufferList();

  /** default construct */
  public ASTFProgram() {
    this(SideType.Client);
  }

  /**
   * construct
   *
   * @param side
   */
  public ASTFProgram(SideType side) {
    this(null, side);
  }

  /**
   * construct
   *
   * @param filePath
   * @param side
   */
  public ASTFProgram(String filePath, SideType side) {
    this(filePath, side, null, true);
  }

  /**
   * Construct
   *
   * @param filePath pcap file absolute path
   * @param side server side or client side
   * @param commands
   * @param stream
   */
  public ASTFProgram(String filePath, SideType side, List<ASTFCmd> commands, boolean stream) {
    this.filePath = filePath;
    this.side = side;
    this.commands = commands;
    this.stream = stream;
    fields.put(COMMANDS, new ArrayList<ASTFCmd>());
    if (filePath != null) {
      CpcapReader cap = CapHandling.cpcapReader(filePath);
      cap.analyze();
      this.payloadLen = cap.payloadLen();
      if (cap.isTcp()) {
        cap.condensePktData();
      } else {
        this.stream = false;
      }

      createCmdFromCap(cap.isTcp(), cap.getPkts(), cap.getPktTimes(), cap.getPktDirs(), side);
    } else if (commands != null) {
      setCmds(commands);
    }
  }

  private void createCmdFromCap(
      boolean isTcp,
      List<CPacketData> cmds,
      List<Double> times,
      List<SideType> dirs,
      SideType initSide) {
    if (cmds.size() != dirs.size()) {
      throw new IllegalStateException(
          String.format("cmds size %s is not equal to dirs size %s", cmds.size(), dirs.size()));
    }
    if (cmds.size() == 0) {
      return;
    }

    List<ASTFCmd> newCmds = new ArrayList();
    int totRcvBytes = 0;
    boolean rx = false;
    int maxDelay = 0;

    if (isTcp) {
      // In case that server start sending the traffic we must wait for the connection to establish
      if (dirs.get(0) == SideType.Server && initSide == SideType.Server) {
        newCmds.add(new ASTFCmdConnect());
      }

      ASTFCmd newCmd = null;
      for (int i = 0; i < cmds.size(); i++) {
        SideType dir = dirs.get(i);
        CPacketData cmd = cmds.get(i);

        if (dir == initSide) {
          newCmd = new ASTFCmdSend(cmd.getPayload());
        } else {
          totRcvBytes += cmd.getPayload().length;
          newCmd = new ASTFCmdRecv(totRcvBytes, false);
        }
        newCmds.add(newCmd);
      }
    } else {
      if (cmds.size() != times.size()) {
        throw new IllegalStateException(
            String.format("cmds size %s is not equal to times size %s", cmds.size(), times.size()));
      }

      SideType lastDir = null;
      for (int i = 0; i < cmds.size(); i++) {
        SideType dir = dirs.get(i);
        CPacketData cmd = cmds.get(i);
        Double time = times.get(i);

        if (dir == initSide) {
          if (lastDir == initSide) {
            int dUsec = (int) (time * 1000000);
            if (dUsec > MAX_DELAY) {
              dUsec = MAX_DELAY;
            }
            if (dUsec > MIN_DELAY) {
              ASTFCmdDelay nCmd = new ASTFCmdDelay(dUsec);
              if (maxDelay < dUsec) {
                maxDelay = dUsec;
              }
              newCmds.add(nCmd);
            }
          } else {
            if (rx) {
              rx = false;
              ASTFCmdRecvMsg nCmd = new ASTFCmdRecvMsg(totRcvBytes, false);
              newCmds.add(nCmd);
            }
          }
        } else {
          totRcvBytes += 1;
          rx = true;
        }
        lastDir = dir;
      }
    }
    if (rx) {
      rx = false;
      ASTFCmdRecvMsg nCmd = new ASTFCmdRecvMsg(totRcvBytes, false);
      newCmds.add(nCmd);
    }
    if (maxDelay > MAX_KEEPALIVE) {
      newCmds.add(0, new ASTFCmdKeepaliveMsg(maxDelay * 2));
    }
    this.setCmds(newCmds);
  }

  private void setCmds(List<ASTFCmd> commands) {
    for (ASTFCmd cmd : commands) {
      if (null != cmd.isBuffer() && cmd.isBuffer()) {
        if (cmd instanceof ASTFCmdTxPkt) {
          ASTFCmdTxPkt txPktCmd = (ASTFCmdTxPkt) cmd;
          totalSendBytes += txPktCmd.getBufLen();
          txPktCmd.setbufIndex(ASTFProgram.bufList.add(txPktCmd.buf()));
        }
        if (cmd instanceof ASTFCmdSend) {
          ASTFCmdSend sendCmd = (ASTFCmdSend) cmd;
          totalSendBytes += sendCmd.getBufLen();
          sendCmd.setbufIndex(ASTFProgram.bufList.add(sendCmd.buf()));
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
  public void updateKeepAlive(ASTFProgram progS) {
    if (fields.get(COMMANDS).size() > 0) {
      ASTFCmd cmd = fields.get(COMMANDS).get(0);
      if (cmd instanceof ASTFCmdKeepaliveMsg) {
        progS.fields.get(COMMANDS).add(0, cmd);
      }
    }
  }

  /**
   * delay for a random time betwean min-max usec with uniform distribution
   *
   * @param minUsec
   * @param maxUsec
   */
  public void delayRand(int minUsec, int maxUsec) {
    if (minUsec > maxUsec) {
      throw new IllegalStateException(
          String.format("minUsec %d is bigger than maxUsec %d", minUsec, maxUsec));
    }
    fields.get(COMMANDS).add(new ASTFCmdDelayRnd(minUsec, maxUsec));
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
    fields.get(COMMANDS).add(new ASTFCmdDelay(usec));
  }

  /**
   * send (l7_buffer) over TCP and wait for the buffer to be acked by peer. Rx side could work in
   * parallel
   *
   * <p>example1 send (buffer1) send (buffer2)
   *
   * <p>Will behave differently than
   *
   * <p>example1 send (buffer1+ buffer2)
   *
   * <p>in the first example there would be PUSH in the last byte of the buffer and immediate ACK
   * from peer while in the last example the buffer will be sent together (might be one segment)
   *
   * @param buf l7 stream as string
   */
  public void send(String buf) {
    // we support bytes or ascii strings
    ASTFCmdSend cmd = null;
    try {
      cmd = new ASTFCmdSend(buf.getBytes("ascii"));
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
    ASTFCmdTxPkt cmd = null;
    try {
      cmd = new ASTFCmdTxPkt(buf.getBytes("ascii"));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Unsupported Encoding Exception", e);
    }
    this.totalSendBytes += cmd.getBufLen();
    cmd.setbufIndex(bufList.add(cmd.buf()));
    fields.get(COMMANDS).add(cmd);
  }

  /**
   * Send l7_buffer by splitting it into small chunks and issue a delay betwean each chunk. This is
   * a utility command that works on top of send/delay command
   *
   * <p>example1: send (buffer1,100,10) will split the buffer to buffers of 100 bytes with delay of
   * 10usec
   *
   * @param l7Buf l7 stream as string
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
    fields.get(COMMANDS).add(new ASTFCmdRecv(totalRcvBytes, clear));
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
   * @param pkts wait until the rx packet watermark is reached on flow counter.
   * @param clear when reach the watermark clear the flow counter
   */
  public void recvMsg(int pkts, boolean clear) {
    this.totalRcvBytes += pkts;
    fields.get(COMMANDS).add(new ASTFCmdRecvMsg(this.totalRcvBytes, clear));
  }

  /** For TCP connection send RST to peer. Should be the last command */
  public void reset() {
    fields.get(COMMANDS).add(new ASTFCmdReset());
  }

  /**
   * For TCP connection wait for peer side to close (read==0) and only then close. Should be the
   * last command This simulates server side that waits for a requests until client retire with
   * close().
   */
  public void waitForPeerClose() {
    fields.get(COMMANDS).add(new ASTFCmdNoClose());
  }

  /**
   * for TCP connection wait for the connection to be connected. should be the first command in the
   * client side
   */
  public void connect() {
    fields.get(COMMANDS).add(new ASTFCmdConnect());
  }

  /** close msg,explicit UDP flow close */
  public void closeMsg() {
    this.fields.get(COMMANDS).add(new ASTFCmdCloseMsg());
  }

  /**
   * set_send_blocking (block), set the stream transmit mode block : for send command wait until the
   * last byte is ack non-block: continue to the next command when the queue is almost empty, this
   * is good for pipeline the transmit
   *
   * @param block
   */
  public void setSendBlocking(boolean block) {
    int flags = block ? 0 : 1;
    this.fields.get(COMMANDS).add(new ASTFCmdTxMode(flags));
  }

  public void setKeepAliveMsg(int msec) {
    this.fields.get(COMMANDS).add(new ASTFCmdRecvMsg(this.totalRcvBytes, false));
  }

  /**
   * set var command
   *
   * @param varId
   * @param value
   */
  public void setVar(String varId, int value) {
    addVar(varId);
    fields.get(COMMANDS).add(new ASTFCmdSetVal(varId, (long) value));
  }

  /**
   * set var command
   *
   * @param varId
   * @param value
   */
  public void setVar(String varId, Long value) {
    addVar(varId);
    fields.get(COMMANDS).add(new ASTFCmdSetVal(varId, value));
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
    fields.get(COMMANDS).add(new ASTFCmdJmpnz(varId, 0, label));
  }

  /**
   * get the total send bytes of the program
   *
   * @return sent bytes
   */
  public int getTotalSendBytes() {
    return totalSendBytes;
  }

  /**
   * return true if it's stream
   *
   * @return if stream
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

  /** class reset, clear all cached data */
  public static void classReset() {
    bufList = new BufferList();
  }

  /**
   * get buffer list size
   *
   * @return buffer size
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
    for (ASTFCmd cmd : fields.get(COMMANDS)) {
      jsonArray.add(cmd.toJson());
    }
    jsonObject.add(COMMANDS, jsonArray);
    return jsonObject;
  }

  /**
   * get payload length
   *
   * @return payload length
   */
  public int getPayloadLen() {
    return payloadLen;
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

  /** update offsets for AstfCmdJmpnz comvert var names to ids */
  private void compile() {
    int i = 0;
    for (ASTFCmd cmd : fields.get(COMMANDS)) {
      if (null != cmd.isStream() && cmd.isStream() != this.stream) {
        throw new IllegalStateException(
            String.format(
                " Command %s stream mode is %s and different from the flow stream mode %s",
                cmd.getName(), cmd.isStream(), this.stream));
      }
      if (cmd instanceof ASTFCmdJmpnz) {
        ASTFCmdJmpnz cmdJmpnz = (ASTFCmdJmpnz) cmd;
        cmdJmpnz.fields.addProperty("offset", getLabelId(cmdJmpnz.getLabel()) - i);
        String id = cmdJmpnz.fields.get("id").getAsString();
        if (!isNumber(id)) {
          cmdJmpnz.fields.addProperty("id", getVarIndex(id));
        }
      }
      if (cmd instanceof ASTFCmdSetVal) {
        String id = cmd.fields.get("id").getAsString();
        if (!isNumber(id)) {
          cmd.fields.addProperty("id", getVarIndex(id));
        }
      }
      i++;
    }
  }

  private boolean isNumber(String str) {
    for (char c : str.toCharArray()) {
      if (c < 48 || c > 57) {
        return false;
      }
    }
    return true;
  }

  /** cached Buffer class for inner use */
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

  /** Side type */
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
    }
  }
}
