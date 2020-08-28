package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import java.util.List;

/** Java implementation for TRex python sdk ASTFProgramCache class */
public class ASTFProgramCache {
  BufferList bufList = new BufferList();

  public ASTFProgramCache() {

    //        try {
    //            bufList=(new
    // BufferList(ASTFProgramCache.class,ASTFProgramCache.class.getMethod("commandsHash")));
    //        } catch (NoSuchMethodException e) {
    //            throw new IllegalStateException("ASTFProgramCache class does not have method
    // commandsHash");
    //        }
  }
  //
  //    /**
  //     * return cmd.buf if type(cmd.buf) is not dict else tuple(cmd.buf.items())
  //     * @param cmd
  //     * @return
  //     */
  //    public static String commandsHash(ASTFCmd cmd){
  //        String buf=null;
  //        boolean isDict=false;
  //        if (cmd instanceof ASTFCmdSend){
  //            ASTFCmdSend sendCmd=(ASTFCmdSend)cmd;
  //            buf= sendCmd.getBase64Buf();
  //            isDict=sendCmd.isDict();
  //        }else if (cmd instanceof ASTFCmdTxPkt){
  //            ASTFCmdTxPkt txPktCmd=(ASTFCmdTxPkt)cmd;
  //            buf= txPktCmd.getBase64Buf();
  //            isDict=txPktCmd.isDict();
  //        }
  //        if (!isDict) {
  //            return buf;
  //        } else {
  //            JsonObject jsonObject = new Gson().fromJson(buf, JsonObject.class);
  //
  //            return jsonObject.toString();
  //        }
  //    }

  public int getLen() {
    return bufList.getLen();
  }

  public void clearCache() {
    bufList = new BufferList();
  }

  public JsonArray toJson() {
    return bufList.toJson();
  }

  public void addCommandsFromProgram(ASTFProgram program) {
    List<ASTFCmd> commands = program.getCommands();
    for (ASTFCmd cmd : commands) {
      if (cmd instanceof ASTFCmdSend) {
        ASTFCmdSend sendCmd = (ASTFCmdSend) cmd;
        try {
          sendCmd.setBufIndex(bufList.add(sendCmd));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (cmd instanceof ASTFCmdTxPkt) {
        ASTFCmdTxPkt txPktCmd = (ASTFCmdTxPkt) cmd;
        try {
          txPktCmd.setBufIndex(bufList.add(txPktCmd));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
