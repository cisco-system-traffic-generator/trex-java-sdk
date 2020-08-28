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
  private int udpMtu;
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

  public ASTFProgram(String filePath, SideType side, int udpMtu) {
    this(filePath, side, null, false, null, udpMtu);
  }

  public ASTFProgram(String filePath, SideType side, List<ASTFCmd> commands, boolean stream) {
    this(filePath, side, commands, stream, null, 0);
  }

  public ASTFProgram(String filePath, SideType side, int udpMtu, ASTFCmd sDelay) {
    this(filePath, side, null, false, sDelay, udpMtu);
  }

  public ASTFProgram(
      String filePath,
      SideType side,
      List<ASTFCmd> commands,
      boolean stream,
      ASTFCmd sDelay,
      int udpMtu) {
    fields.put(COMMANDS, new ArrayList<ASTFCmd>());
    this.sDelay = sDelay;
    this.udpMtu = udpMtu;
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

  public void updateKeepalive(ASTFProgram progS) {
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

  public void closeMsg() {
    fields.get(COMMANDS).add(new ASTFCmdCloseMsg());
  }

  public void sendMsg(String buf) {
    sendMsg(buf, 0, null);
  }

  public void sendMsg(String buf, int size) {
    sendMsg(buf, size, null);
  }

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
    txPktCmd.setBufIndex(0);
    fields.get(COMMANDS).add(txPktCmd);
  }

  public void setSendBlocking(boolean block) {
    int flags = block ? 0 : 1;
    fields.get(COMMANDS).add(new ASTFCmdTxMode(flags));
  }

  public void setKeepaliveMsg(int msec) {
    if (msec < 0) {
      throw new IllegalStateException(String.format("usec %d is less than 0", msec));
    }
    fields.get(COMMANDS).add(new ASTFCmdKeepaliveMsg(msec));
  }

  public void recvMsg(int pkts) {
    recvMsg(pkts, false);
  }

  public void recvMsg(int pkts, boolean clear) {
    if (pkts < 0) {
      throw new IllegalStateException(String.format("usec %d is less than 0", pkts));
    }
    totalRcvBytes += pkts;
    fields.get(COMMANDS).add(new ASTFCmdRecvMsg(pkts, clear));
    if (clear) {
      totalRcvBytes = 0;
    }
  }

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
    cmd.setBufIndex(0);
    fields.get(COMMANDS).add(cmd);
  }

  public void recv(int bytes) {
    recv(bytes, false);
  }

  public void recv(int bytes, boolean clear) {
    totalRcvBytes += bytes;
    fields.get(COMMANDS).add(new ASTFCmdRecv(totalRcvBytes, clear));
    if (clear) {
      totalRcvBytes = 0;
    }
  }

  public void reset() {
    fields.get(COMMANDS).add(new ASTFCmdReset());
  }

  public void waitForPeerClose() {
    fields.get(COMMANDS).add(new ASTFCmdNoClose());
  }

  public void connect() {
    fields.get(COMMANDS).add(new ASTFCmdConnect());
  }

  public void accept() {
    fields.get(COMMANDS).add(new ASTFCmdConnect());
  }

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

  public void setVar(String varId, Long val) {
    addVar(varId);
    fields.get(COMMANDS).add(new ASTFCmdSetVal(varId, val));
  }

  public void setTickVar(String varId) {
    addVar(varId);
    fields.get(COMMANDS).add(new ASTFCmdSetTickVar(varId));
  }

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

  public void jmpNz(String varId, String label) {
    fields.get(COMMANDS).add(new ASTFCmdJMPNZ(varId, 0, label));
  }

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
    int totalRcvBytes = 0;
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
          if (lastDir.equals(initSide)) {
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

      if (maxDelay > -900000) {
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
          txPktCmd.setBufIndex(0); // check
        }
        if (cmd instanceof ASTFCmdSend) {
          ASTFCmdSend sendCmd = (ASTFCmdSend) cmd;
          totalSendBytes += sendCmd.getBufLen();
          sendCmd.setBufIndex(0); // check
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

  public int getTotalSendBytes() {
    return (int) totalSendBytes;
  }

  public long getTotalRcvBytesLong() {
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
