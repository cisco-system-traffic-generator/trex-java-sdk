package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdJmpnz class */
public class ASTFCmdJmpnz extends ASTFCmd {
  private static final String NAME = "jmp_nz";

  private String label;

  /**
   * construct
   *
   * @param idVal
   * @param offset
   * @param label
   */
  public ASTFCmdJmpnz(String idVal, int offset, String label) {
    super();
    this.label = label;
    fields.addProperty("name", NAME);
    fields.addProperty("id", idVal);
    fields.addProperty("offset", offset);
  }

  @Override
  public String getName() {
    return NAME;
  }

  /**
   * get label
   *
   * @return label
   */
  public String getLabel() {
    return label;
  }
}
