package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdKeepaliveMsg class */
public class ASTFCmdKeepaliveMsg extends ASTFCmd {
  private static final String NAME = "keepalive";

  /**
   * construct
   *
   * @param msec
   */
  public ASTFCmdKeepaliveMsg(int msec) {
    super();
    fields.addProperty("name", NAME);
    fields.addProperty("msec", msec);
    stream = false;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
