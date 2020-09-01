package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdJMPBase class */
public abstract class ASTFCmdJMPBase extends ASTFCmd {
  protected String label;

  public String getLabel() {
    return label;
  }

  /**
   * constructor
   *
   * @param idVal
   * @param offset
   * @param label
   */
  public ASTFCmdJMPBase(String idVal, long offset, String label) {
    super();
    this.label = label;
    fields.addProperty("id", idVal);
    fields.addProperty("offset", offset);
  }
}
