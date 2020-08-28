package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdSetValBase class */
public abstract class ASTFCmdSetValBase extends ASTFCmd {

  /**
   * constructor
   *
   * @param idVal
   */
  public ASTFCmdSetValBase(String idVal) {
    super();
    fields.addProperty("id", idVal);
  }
}
