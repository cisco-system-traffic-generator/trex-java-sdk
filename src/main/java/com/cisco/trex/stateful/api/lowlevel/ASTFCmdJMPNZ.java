package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdJmpnz class */
public class ASTFCmdJMPNZ extends ASTFCmdJMPBase {
  private static final String NAME = "jmp_nz";

  /**
   * constructor
   *
   * @param idVal
   * @param offset
   * @param label
   */
  public ASTFCmdJMPNZ(String idVal, int offset, String label) {
    super(idVal, offset, label);
    fields.addProperty("name", NAME);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
