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
  //    Method hashFunction; //hash function
  //    Class clz;// ASTFTemplateCache and ASTFProgramCache,
  //    public BufferList(Class clz,Method hashFunction){
  //        this.clz=clz;
  //        this.hashFunction=hashFunction;
  //    }

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
    boolean isDict = false;
    if (cmd instanceof ASTFCmdSend) {
      ASTFCmdSend sendCmd = (ASTFCmdSend) cmd;
      buf = sendCmd.getBase64Buf();
      isDict = sendCmd.isDict();
    } else if (cmd instanceof ASTFCmdTxPkt) {
      ASTFCmdTxPkt txPktCmd = (ASTFCmdTxPkt) cmd;
      buf = txPktCmd.getBase64Buf();
      isDict = txPktCmd.isDict();
    }
    if (!isDict) {
      return buf;
    } else {
      JsonObject jsonObject = new Gson().fromJson(buf, JsonObject.class);

      return jsonObject.toString();
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
   * @param new_buf
   * @return index of the added buffer
   */
  public int add(Object new_buf) throws Exception {
    String m = "";
    if (new_buf instanceof ASTFCmd) {
      m = commandsHash((ASTFCmd) new_buf);
    } else if (new_buf instanceof ASTFProgram) {
      m = programsHash((ASTFProgram) new_buf);
    }
    if (bufHash.containsKey(m)) {
      return bufHash.get(m);
    }

    if (new_buf instanceof ASTFCmdSend) {
      ASTFCmdSend astfCmdSend = (ASTFCmdSend) new_buf;
      bufList.add(astfCmdSend.getBase64Buf());
    } else if (new_buf instanceof ASTFCmdTxPkt) {
      ASTFCmdTxPkt astfCmdTxPkt = (ASTFCmdTxPkt) new_buf;
      bufList.add(astfCmdTxPkt.getBase64Buf());
    } else {
      bufList.add(new_buf);
    }
    int newIndex = bufList.size() - 1;
    bufHash.put(m, newIndex);
    return newIndex;
  }
  /*
      public int add(Object command){
          String sha256Buf="";
          String cmdBuf="";
          if (command instanceof ASTFCmdSend){
              ASTFCmdSend sendCmd=(ASTFCmdSend)command;
              try {
                  cmdBuf=sendCmd.getBase64Buf();
                  sha256Buf=hashFunction.invoke(ASTFCmdSend.class,cmdBuf).toString();
              } catch (Exception e) {
                  throw new IllegalStateException(String.format("Class ASTFCmdSend dont have method%",hashFunction));
              }
          }
          if (command instanceof ASTFCmdTxPkt){
              ASTFCmdTxPkt txPktCmd=(ASTFCmdTxPkt)command;
              try {
                  cmdBuf=txPktCmd.getBase64Buf();
                  sha256Buf=hashFunction.invoke(ASTFCmdTxPkt.class,cmdBuf).toString();
              } catch (Exception e) {
                  throw new IllegalStateException(String.format("Class ASTFCmdTxPkt dont have method%",hashFunction));
              }
          }
          if (command instanceof ASTFProgram){
              ASTFProgram program=(ASTFProgram)command;
              try {
                  sha256Buf=hashFunction.invoke(ASTFProgram.class,program.toJson().toString()).toString();
              } catch (Exception e) {
                  throw new IllegalStateException(String.format("Class ASTFProgram dont have method%",hashFunction));
              }
          }
          if (bufHash.containsKey(sha256Buf)) {
              return bufHash.get(sha256Buf);
          }else{
              if (command instanceof ASTFCmd){
                  bufList.add(cmdBuf);
              }else if(command instanceof ASTFProgram){
                  bufList.add((ASTFProgram)command);
              }
              int newIndex=bufList.size()-1;
              bufHash.put(sha256Buf,newIndex);
              return newIndex;
          }
      }

  */
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
