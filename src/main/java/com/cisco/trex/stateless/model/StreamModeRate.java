package com.cisco.trex.stateless.model;

public class StreamModeRate {

  private Type type;

  private Double value;

  public StreamModeRate(Type type, Double value) {
    this.type = type;
    this.value = value;
  }

  public Type getType() {
    return type;
  }

  public Double getValue() {
    return value;
  }

  public enum Type {
    pps("pps"),
    bps_L1("bps_L1"),
    bps_L2("bps_L2"),
    percentage("percentage");
    String name;

    Type(String name) {
      this.name = name;
    }
  }
}
