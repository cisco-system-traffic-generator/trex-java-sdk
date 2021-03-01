package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

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
  private boolean stream = true;
  private static final int MIN_DELAY = 50;
  private static final int MAX_DELAY = 700000;
  private static final int MAX_KEEPALIVE = 500000;
  private static final String COMMANDS = "commands";

  private Map<String, Integer> vars = new HashMap<>();
  private Map<String, Integer> labels = new HashMap<>();
  private Map<String, List<ASTFCmd>> fields = new HashMap<>();
  private long totalSendBytes = 0;
  private long totalRcvBytes = 0;
  private Integer udpMtu;
  private ASTFCmd sDelay;
  private int payloadLen = 0;

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
    this(filePath, side, 0);
  }

  public ASTFProgram(String filePath, SideType side, Integer udpMtu) {
    this(filePath, side, null, true, null, udpMtu);
  }

  public ASTFProgram(String filePath, SideType side, List<ASTFCmd> commands, boolean stream) {
    this(filePath, side, commands, stream, null, 0);
  }

  public ASTFProgram(String filePath, SideType side, Integer udpMtu, ASTFCmd sDelay) {
    this(filePath, side, null, true, sDelay, udpMtu);
  }

  public ASTFProgram(
      String filePath,
      SideType side,
      List<ASTFCmd> commands,
      boolean stream,
      ASTFCmd sDelay,
      Integer udpMtu) {
    fields.put(COMMANDS, new ArrayList<ASTFCmd>());
    this.sDelay = sDelay;
    this.udpMtu = udpMtu;
    this.stream = stream;
    if (!StringUtils.isEmpty(filePath)) {
      CpcapReader cap = CapHandling.cpcapReader(filePath);
      cap.analyze();

      this.payloadLen = cap.payloadLen();
      if (cap.isTcp()) {
        cap.condensePktData();
      } else {
        this.stream = false;
      }
      createCmdsFromCap(cap.isTcp(), cap.getPkts(), cap.getPktTimes(), cap.getPktDirs(), side);
    } else if (commands != null && !commands.isEmpty()) {
      setCmds(commands);
    }
  }

  /**
   * In case of pcap file need to copy the keepalive command from client to server side
   *
   * @param progS
   */
  public void updateKeepAlive(ASTFProgram progS) {
    if (!fields.get(COMMANDS).isEmpty()) {
      ASTFCmd cmd = fields.get(COMMANDS).get(0);
      if (cmd instanceof ASTFCmdKeepaliveMsg) {
        progS.fields.get(COMMANDS).add(0, cmd);
      }
    }
  }

  public boolean isStream() {
    return stream;
  }

  /**
   * Send l7_buffer by splitting it into small chunks and issue a delay betwean each chunk. This is
   * a utility command that works on top of send/delay command
   *
   * <p>example1 send (buffer1,100,10) will split the buffer to buffers of 100 bytes with delay of
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
        send(l7Buf.substring(cnt, cnt + chunkSize));
      } else {
        send(l7Buf.substring(cnt, l7Buf.length()));
      }
      if (delayUsec > 0) {
        delay(delayUsec);
      }
      cnt += chunkSize;
      size -= chunkSize;
    }
  }

  /** explicit UDP flow close */
  public void closeMsg() {
    fields.get(COMMANDS).add(new ASTFCmdCloseMsg());
  }

  public void sendMsg(String buf) {
    sendMsg(buf, 0, null);
  }

  public void sendMsg(String buf, int size) {
    sendMsg(buf, size, null);
  }

  /**
   * send UDP message (buf)
   *
   * <p>example1 send_msg (buffer1) recv_msg (1)
   *
   * @param buf l7 stream as string
   * @param size udp payload size, effective only when size > len(buf).
   * @param fill l7 stream filled by string, only if size is effective.
   */
  public void sendMsg(String buf, int size, String fill) {
    ASTFCmdTxPkt txPktCmd = null;
    try {
      txPktCmd =
          new ASTFCmdTxPkt(
              buf.getBytes("ascii"), size, fill != null ? fill.getBytes("ascii") : null);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Unsupported Encoding Exception", e);
    }

    totalSendBytes += txPktCmd.getBufLen();
    txPktCmd.setBufIndex(-1);
    fields.get(COMMANDS).add(txPktCmd);
  }

  /**
   * set the stream transmit mode
   *
   * <p>block : for send command wait until the last byte is ack
   *
   * <p>non-block: continue to the next command when the queue is almost empty, this is good for
   * pipeline the transmit
   *
   * @param block
   */
  public void setSendBlocking(boolean block) {
    int flags = block ? 0 : 1;
    fields.get(COMMANDS).add(new ASTFCmdTxMode(flags));
  }

  /**
   * set the keepalive timer for UDP flows
   *
   * @param msec the keepalive time in msec
   */
  public void setKeepAliveMsg(int msec) {
    if (msec < 0) {
      throw new IllegalStateException(String.format("usec %d is less than 0", msec));
    }
    fields.get(COMMANDS).add(new ASTFCmdKeepaliveMsg(msec));
  }

  /**
   * recv msg, works for UDP flow
   *
   * @param pkts wait until the rx packet watermark is reached on flow counter.
   * @deprecated use method with long instead
   */
  @Deprecated
  public void recvMsg(int pkts) {
    recvMsg((long) pkts, false);
  }

  /**
   * recv Msg cmd
   *
   * @param pkts wait until the rx packet watermark is reached on flow counter.
   * @param clear when reach the watermark clear the flow counter @Deprecated use method with long
   *     instead
   * @deprecated use method with long instead
   */
  @Deprecated
  public void recvMsg(int pkts, boolean clear) {
    recvMsg((long) pkts, clear);
  }

  public void recvMsg(long pkts) {
    recvMsg(pkts, false);
  }

  /**
   * works for UDP flow
   *
   * @param pkts wait until the rx packet watermark is reached on flow counter.
   * @param clear when reach the watermark clear the flow counter
   */
  public void recvMsg(long pkts, boolean clear) {
    if (pkts < 0) {
      throw new IllegalStateException(String.format("usec %d is less than 0", pkts));
    }
    totalRcvBytes += pkts;
    fields.get(COMMANDS).add(new ASTFCmdRecvMsg(totalRcvBytes, clear));
    if (clear) {
      totalRcvBytes = 0;
    }
  }

  /**
   * delay for x usec
   *
   * @param delayUsec delay for this time in usec
   */
  public void delay(int delayUsec) {
    if (delayUsec < 0) {
      throw new IllegalStateException(String.format("usec %d is less than 0", delayUsec));
    }
    fields.get(COMMANDS).add(new ASTFCmdDelay(delayUsec));
  }

  public void send(String buf) {
    send(buf, 0, null);
  }

  public void send(String buf, int size) {
    send(buf, size, null);
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
   * @param size total size of l7 stream, effective only when size > len(buf).
   * @param fill l7 stream filled by string, only if size is effective.
   */
  public void send(String buf, int size, String fill) {
    ASTFCmdSend cmd = null;
    try {
      cmd =
          new ASTFCmdSend(
              buf.getBytes("ascii"), size, fill != null ? fill.getBytes("ascii") : null);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Unsupported Encoding Exception", e);
    }
    totalSendBytes += cmd.getBufLen();
    cmd.setBufIndex(-1);
    fields.get(COMMANDS).add(cmd);
  }

  /**
   * recv bytes command
   *
   * @param bytes @Deprecated use method with Long instead
   */
  @Deprecated
  public void recv(int bytes) {
    recv((long) bytes, false);
  }

  /**
   * recv bytes command
   *
   * @param bytes
   * @param clear @Deprecated use method with long instead
   */
  @Deprecated
  public void recv(int bytes, boolean clear) {
    recv((long) bytes, clear);
  }

  public void recv(long bytes) {
    recv(bytes, false);
  }

  /**
   * @param bytes wait until the rx bytes watermark is reached on flow counter.
   * @param clear when reach the watermark clear the flow counter
   */
  public void recv(long bytes, boolean clear) {
    totalRcvBytes += bytes;
    fields.get(COMMANDS).add(new ASTFCmdRecv(totalRcvBytes, clear));
    if (clear) {
      totalRcvBytes = 0;
    }
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

  /**
   * for TCP connection wait for the connection to be accepted. should be the first command in the
   * server side
   */
  public void accept() {
    fields.get(COMMANDS).add(new ASTFCmdConnect());
  }

  /**
   * delay for a random time betwean min-max usec with uniform distribution
   *
   * @param minUsec min delay for this time in usec
   * @param maxUsec max delay for this time in usec
   */
  public void delayRand(int minUsec, int maxUsec) {
    if (minUsec > maxUsec) {
      throw new IllegalStateException(
          String.format("minUsec %d is bigger than maxUsec %d", minUsec, maxUsec));
    }
    fields.get(COMMANDS).add(new ASTFCmdDelayRnd(minUsec, maxUsec));
  }

  private void addVar(String varName) {
    if (!vars.containsKey(varName)) {
      int index = vars.size();
      vars.put(varName, index);
    }
  }

  private int getVarIndex(String varName) {
    if (!vars.containsKey(varName)) {
      throw new IllegalStateException(String.format("varName %s is not existed", varName));
    }
    return vars.get(varName);
  }

  /**
   * set var command
   *
   * @param varId
   * @param val
   * @deprecated use method with long instead
   */
  @Deprecated
  public void setVar(String varId, int val) {
    setVar(varId, (long) val);
  }

  /**
   * Set a flow variable
   *
   * @param varId var-id there are limited number of variables
   * @param val value of the variable
   */
  public void setVar(String varId, Long val) {
    addVar(varId);
    fields.get(COMMANDS).add(new ASTFCmdSetVal(varId, val));
  }

  /**
   * Set a flow variable used with jmp_nz command. Timer will be started when declaring tick var.
   *
   * @param varId var-id there are limited number of variables
   */
  public void setTickVar(String varId) {
    addVar(varId);
    fields.get(COMMANDS).add(new ASTFCmdSetTickVar(varId));
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

  private int getLabelId(String label) {
    if (!labels.containsKey(label)) {
      throw new IllegalStateException(String.format("label %s was not defined", label));
    }
    return labels.get(label);
  }

  /**
   * Decrement the flow variable, in case of none zero jump to label
   *
   * @param varId flow var id
   * @param label label id
   */
  public void jmpNz(String varId, String label) {
    fields.get(COMMANDS).add(new ASTFCmdJMPNZ(varId, 0, label));
  }

  /**
   * Check the time passed from flow variable, in case of time passed is less then duration jump to
   * label.
   *
   * @param varId flow var id
   * @param label label id
   * @param duration duration of time in seconds
   */
  public void jmpDp(String varId, String label, long duration) {
    fields.get(COMMANDS).add(new ASTFCmdJMPDP(varId, 0, label, duration));
  }

  private void createCmdsFromCap(
      boolean isTcp,
      List<CPacketData> cmds,
      List<Double> times,
      List<SideType> dirs,
      SideType initSide) {
    if (cmds.isEmpty()) {
      return;
    }
    if (cmds.size() != dirs.size()) {
      throw new IllegalStateException(
          String.format("cmds size %s is not equal to dirs size %s", cmds.size(), dirs.size()));
    }

    List<ASTFCmd> newCmds = new ArrayList<>();
    long totalRcvBytes = 0;
    boolean rx = false;
    int maxDelay = 0;

    if (isTcp) {
      if (dirs.get(0).equals(SideType.Server) && initSide.equals(SideType.Server)) {
        newCmds.add(new ASTFCmdConnect());
      }

      for (int i = 0; i < cmds.size(); i++) {
        ASTFCmd newCmd;
        byte[] cmdPayload = cmds.get(i).getPayload();
        if (dirs.get(i).equals(initSide)) {
          newCmd = new ASTFCmdSend(cmdPayload);
        } else {
          totalRcvBytes += cmdPayload.length;
          newCmd = new ASTFCmdRecv(totalRcvBytes, false);
        }
        newCmds.add(newCmd);
      }
    } else {
      if (cmds.size() != times.size()) {
        throw new IllegalStateException(
            String.format("cmds size %s is not equal to times size %s", cmds.size(), dirs.size()));
      }
      SideType lastDir = null;
      int nonL7Len = 14 + 20 + 8;
      if (this.udpMtu < nonL7Len) {
        throw new IllegalStateException(
            String.format("udp_mtu is %s is smaller than sum of L2-4 (%s)", this.udpMtu, nonL7Len));
      }
      int maxPayloadAllowed = this.udpMtu == 0 ? Integer.MAX_VALUE : this.udpMtu - nonL7Len;
      for (int i = 0; i < cmds.size(); i++) {
        CPacketData cmd = cmds.get(i);
        if (cmd.getPayload().length > maxPayloadAllowed) {
          byte[] subPayload = new byte[maxPayloadAllowed];
          System.arraycopy(cmd.getPayload(), 0, subPayload, 0, maxPayloadAllowed);
          cmd.setPayload(subPayload);
        }

        SideType dir = dirs.get(i);
        Double time = times.get(i);
        if (dir.equals(initSide)) {
          ASTFCmd ncmd;
          if (lastDir != null && lastDir.equals(initSide)) {
            int dUsec = (int) (time * 1000000);
            dUsec = dUsec > MAX_DELAY ? MAX_DELAY : dUsec;
            if (dUsec > MIN_DELAY) {
              ncmd = new ASTFCmdDelay(dUsec);
              maxDelay = maxDelay < dUsec ? dUsec : maxDelay;
              newCmds.add(ncmd);
            }
          } else {
            if (rx) {
              rx = false;
              ncmd = new ASTFCmdRecvMsg(totalRcvBytes, false);
              newCmds.add(ncmd);
            }
          }
          newCmds.add(new ASTFCmdTxPkt(cmd.getPayload()));
        } else {
          totalRcvBytes += 1;
          rx = true;
        }
        lastDir = dir;
      }
    }

    if (rx) {
      rx = false;
      ASTFCmdRecvMsg ncmd = new ASTFCmdRecvMsg(totalRcvBytes, false);
      newCmds.add(ncmd);
    }
    if (maxDelay > MAX_KEEPALIVE) {
      newCmds.add(0, new ASTFCmdKeepaliveMsg(maxDelay * 2));
    }

    if (sDelay != null && initSide.equals(SideType.Server)) {
      newCmds = addServerDelay(newCmds);
    }

    if (!isTcp) {
      maxDelay = 0;
      for (ASTFCmd cmd : newCmds) {
        if (cmd instanceof ASTFCmdDelay) {
          maxDelay = Math.max(maxDelay, ((ASTFCmdDelay) cmd).getUsec());
        }
        if (cmd instanceof ASTFCmdDelayRnd) {
          maxDelay = Math.max(maxDelay, ((ASTFCmdDelayRnd) cmd).getMaxUsec());
        }
      }

      if (maxDelay >= 900000) {
        newCmds.add(0, new ASTFCmdKeepaliveMsg((int) (maxDelay / 1000 * 1.5)));
      }
    }
    setCmds(newCmds);
  }

  private void setCmds(List<ASTFCmd> cmds) {
    for (ASTFCmd cmd : cmds) {
      if (cmd.isBuffer() != null && cmd.isBuffer()) {
        if (cmd instanceof ASTFCmdTxPkt) {
          ASTFCmdTxPkt txPktCmd = (ASTFCmdTxPkt) cmd;
          totalSendBytes += txPktCmd.getBufLen();
          txPktCmd.setBufIndex(-1);
        }
        if (cmd instanceof ASTFCmdSend) {
          ASTFCmdSend sendCmd = (ASTFCmdSend) cmd;
          totalSendBytes += sendCmd.getBufLen();
          sendCmd.setBufIndex(-1);
        }
      }
      fields.get(COMMANDS).add(cmd);
    }
  }

  private List<ASTFCmd> addServerDelay(List<ASTFCmd> cmds) {
    List<String> rxCmds = new ArrayList<>();
    rxCmds.add("rx");
    rxCmds.add("rx_msg");

    List<String> txCmds = new ArrayList<>();
    txCmds.add("tx");
    txCmds.add("tx_msg");

    String lastRxCmd = null;
    List<ASTFCmd> newCmds = new ArrayList<>();
    for (ASTFCmd cmd : cmds) {
      String currCmd = cmd.getName();
      if (rxCmds.contains(currCmd)) {
        lastRxCmd = currCmd;
      }
      if (txCmds.contains(currCmd) && currCmd.replaceAll("tx", "rx").equals(lastRxCmd)) {
        newCmds.add(sDelay);
        lastRxCmd = null;
      }
      newCmds.add(cmd);
    }
    return newCmds;
  }

  private void compile() {
    int i = 0;
    for (ASTFCmd cmd : fields.get(COMMANDS)) {
      if (cmd.isStream() != null && cmd.isStream() != stream) {
        throw new IllegalStateException(
            String.format(
                " Command %s stream mode is %s and different from the flow stream mode %s",
                cmd.getName(), cmd.isStream(), this.stream));
      }
      if (cmd instanceof ASTFCmdJMPBase) {
        ASTFCmdJMPBase jMPBaseCmd = (ASTFCmdJMPBase) cmd;
        jMPBaseCmd.fields.addProperty("offset", getLabelId(jMPBaseCmd.label) - i);
        String id = cmd.fields.get("id").getAsString();
        if (!isNumber(id)) {
          cmd.fields.addProperty("id", getVarIndex(id));
        }
      }

      if (cmd instanceof ASTFCmdSetValBase) {
        String idName = cmd.fields.get("id").getAsString();
        if (!isNumber(idName)) {
          cmd.fields.addProperty("id", getVarIndex(idName));
        }
      }
      i++;
    }
  }

  public JsonObject toJson() {
    compile();
    JsonObject jsonObject = new JsonObject();
    JsonArray jsonArray = new JsonArray();
    for (ASTFCmd cmd : fields.get(COMMANDS)) {
      jsonArray.add(cmd.toJson());
    }
    jsonObject.add(COMMANDS, jsonArray);
    if (!stream) {
      jsonObject.addProperty("stream", false);
    }
    return jsonObject;
  }

  public List<ASTFCmd> getCommands() {
    return fields.get(COMMANDS);
  }

  public int getPayloadLen() {
    return payloadLen;
  }

  public long getTotalSendBytes() {
    return totalSendBytes;
  }

  public long getTotalRcvBytes() {
    return totalRcvBytes;
  }

  private static boolean isNumber(String str) {
    for (char c : str.toCharArray()) {
      if (c < 48 || c > 57) {
        return false;
      }
    }
    return true;
  }
  /**
   * @param buf should be base64 encode string
   * @return Hex string of the sha256 encode buf
   */
  public static String encodeSha256(String buf) {
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
