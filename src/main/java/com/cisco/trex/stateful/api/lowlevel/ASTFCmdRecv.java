package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdRecv class */
public class ASTFCmdRecv extends ASTFCmd {
  private static final String NAME = "rx";

  /**
   * construct
   *
   * @param minBytes minimal receive bytes
   * @param clear true if clear data
   */
  public ASTFCmdRecv(long minBytes, boolean clear) {
    super();
    fields.addProperty("name", NAME);
    fields.addProperty("min_bytes", minBytes);
    if (clear) {
      fields.addProperty("clear", clear);
    }
    stream = true;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
