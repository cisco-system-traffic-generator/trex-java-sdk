package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdJMPDP class */
public class ASTFCmdJMPDP extends ASTFCmdJMPBase {
  private static final String NAME = "jmp_dp";

  /**
   * constructor
   *
   * @param idVal
   * @param offset
   * @param label
   * @param duration
   */
  public ASTFCmdJMPDP(String idVal, int offset, String label, long duration) {
    super(idVal, offset, label);
    fields.addProperty("name", NAME);
    fields.addProperty("duration", duration);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
