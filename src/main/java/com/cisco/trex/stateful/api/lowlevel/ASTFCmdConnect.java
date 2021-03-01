package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdConnect class */
public class ASTFCmdConnect extends ASTFCmd {
  private static final String NAME = "connect";

  /** constructor */
  public ASTFCmdConnect() {
    super();
    fields.addProperty("name", NAME);
    stream = true;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
