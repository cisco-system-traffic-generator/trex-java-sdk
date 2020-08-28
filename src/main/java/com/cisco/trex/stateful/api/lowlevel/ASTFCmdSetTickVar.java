package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdSetTickVar class */
public class ASTFCmdSetTickVar extends ASTFCmdSetValBase {
  private static final String NAME = "set_tick_var";

  /**
   * constructor
   *
   * @param idVal
   */
  public ASTFCmdSetTickVar(String idVal) {
    super(idVal);
    fields.addProperty("name", NAME);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
