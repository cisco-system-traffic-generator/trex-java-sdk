package com.cisco.trex.stateless.model;

public class RxFeature {

  private Type type;

  private Object value;

  public RxFeature(Type type, Object value) {
    this.type = type;
    this.value = value;
  }

  public Type getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }

  public enum Type {
    Queue("queue");

    private String type;

    Type(String type) {
      this.type = type;
    }
  }
}
