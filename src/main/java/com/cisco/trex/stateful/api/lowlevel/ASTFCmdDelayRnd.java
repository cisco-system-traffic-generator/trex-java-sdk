package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdDelayRnd class */
public class ASTFCmdDelayRnd extends ASTFCmd {
  private static final String NAME = "delay_rnd";

  /**
   * constructor
   *
   * @param minUsec minimum delay seconds
   * @param maxUsec maximum delay seconds
   */
  public ASTFCmdDelayRnd(int minUsec, int maxUsec) {
    super();
    fields.addProperty("name", NAME);
    fields.addProperty("min_usec", minUsec);
    fields.addProperty("max_usec", maxUsec);
  }

  @Override
  public String getName() {
    return NAME;
  }

  public int getMinUsec() {
    return fields.get("min_usec").getAsInt();
  }

  public int getMaxUsec() {
    return fields.get("max_usec").getAsInt();
  }
}
