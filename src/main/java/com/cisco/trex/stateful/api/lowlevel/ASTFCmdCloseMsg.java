package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdCloseMsg class */
public class ASTFCmdCloseMsg extends ASTFCmd {
  private static final String NAME = "close_msg";

  /** constructor */
  public ASTFCmdCloseMsg() {
    super();
    fields.addProperty("name", NAME);
    stream = false;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
