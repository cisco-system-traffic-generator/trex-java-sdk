package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Java implementation for TRex python sdk BufferList class */
public class BufferList {
  List<Object> bufList = new ArrayList<>();
  Map<String, Integer> bufHash = new HashMap<>();

  /** Default constructor */
  public BufferList() {}

  /**
   * return cmd.buf if type(cmd.buf) is not dict else tuple(cmd.buf.items())
   *
   * @param cmd
   * @return
   */
  public String commandsHash(ASTFCmd cmd) {
    String buf = null;
    if (cmd instanceof ASTFCmdSend) {
      ASTFCmdSend sendCmd = (ASTFCmdSend) cmd;
      buf = sendCmd.getBase64Buf();
    } else if (cmd instanceof ASTFCmdTxPkt) {
      ASTFCmdTxPkt txPktCmd = (ASTFCmdTxPkt) cmd;
      buf = txPktCmd.buf();
    }

    // the buf is a string or a jsonString, if is a string just return buf,
    // if is a json,return format is ((key1,value1),(key2,value2))
    if (buf.startsWith("{")) {
      String[] tempBuf = buf.substring(1, buf.length() - 1).split(",");
      StringBuffer resultBuf = new StringBuffer("(");
      for (String s : tempBuf) {
        resultBuf.append("(").append(s.replace(":", ",")).append(")");
      }
      resultBuf.append(")");
      return resultBuf.toString();

    } else {
      return buf;
    }
  }

  public String programsHash(ASTFProgram program) {
    return ASTFProgram.encodeSha256(program.toJson().toString());
  }

  /**
   * get buf list length
   *
   * @return buf list length
   */
  public int getLen() {
    return bufList.size();
  }

  /**
   * add, and return index of added buffer
   *
   * @param newBuffer
   * @return index of the added buffer
   */
  public int add(Object newBuffer) throws Exception {
    String m = "";
    if (newBuffer instanceof ASTFCmd) {
      m = commandsHash((ASTFCmd) newBuffer);
    } else if (newBuffer instanceof ASTFProgram) {
      m = programsHash((ASTFProgram) newBuffer);
    }
    if (bufHash.containsKey(m)) {
      return bufHash.get(m);
    }

    if (newBuffer instanceof ASTFCmdSend) {
      ASTFCmdSend astfCmdSend = (ASTFCmdSend) newBuffer;
      bufList.add(astfCmdSend.getBase64Buf());
    } else if (newBuffer instanceof ASTFCmdTxPkt) {
      ASTFCmdTxPkt astfCmdTxPkt = (ASTFCmdTxPkt) newBuffer;
      bufList.add(astfCmdTxPkt.buf());
    } else {
      bufList.add(newBuffer);
    }
    int newIndex = bufList.size() - 1;
    bufHash.put(m, newIndex);
    return newIndex;
  }

  public List<Object> getBufList() {
    return bufList;
  }
  /**
   * to json format
   *
   * @return json string
   */
  public JsonArray toJson() {

    JsonArray jsonArray = new JsonArray();
    for (Object buf : bufList) {
      try {
        JsonObject jsonObj = new Gson().fromJson(buf.toString(), JsonObject.class);
        jsonArray.add(jsonObj);
      } catch (JsonSyntaxException e) {
        jsonArray.add(buf.toString());
      }
    }
    return jsonArray;
  }
}
