package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdReset class */
public class ASTFCmdReset extends ASTFCmd {
  private static final String NAME = "reset";

  /** construct */
  public ASTFCmdReset() {
    fields.addProperty("name", NAME);
    stream = true;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
