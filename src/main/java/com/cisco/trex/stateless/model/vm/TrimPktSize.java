package com.cisco.trex.stateless.model.vm;

public class TrimPktSize extends VMInstruction {

  private String type;
  private String name;

  public TrimPktSize(String name) {
    super();
    this.type = "trim_pkt_size";
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
