package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import java.util.List;

/** Java implementation for TRex python sdk ASTFProgramCache class */
public class ASTFProgramCache {
  private BufferList bufList = new BufferList();

  public ASTFProgramCache() {}

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
      if (cmd.isBuffer()) {
        try {
          bufList.add(cmd);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
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
