package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdSetVal class */
public class ASTFCmdSetVal extends ASTFCmd {
  private static final String NAME = "set_var";

  /**
   * construct
   *
   * @param idVal
   * @param val
   */
  public ASTFCmdSetVal(String idVal, long val) {
    super();
    this.fields.addProperty("name", NAME);
    this.fields.addProperty("id", idVal);
    this.fields.addProperty("val", val);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
