package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdSetVal class */
public class ASTFCmdSetVal extends ASTFCmdSetValBase {
  private static final String NAME = "set_var";

  /**
   * constructor
   *
   * @param idVal
   * @param val
   */
  public ASTFCmdSetVal(String idVal, long val) {
    super(idVal);
    fields.addProperty("name", NAME);
    fields.addProperty("val", val);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
