package com.cisco.trex.stateless.model.vm;

public class FixChecksumHw extends VMInstruction {

  private String type;
  private int l2_len;
  private int l3_len;
  private int l4_type;

  public enum L4Type {
    UDP(11),
    TCP(13),
    IP(17);

    int value;

    L4Type(int value) {
      this.value = value;
    }

    int getValue() {
      return value;
    }
  }

  public FixChecksumHw(int l2_len, int l3_len, L4Type l4_type) {
    super();
    this.type = "fix_checksum_hw";
    this.l2_len = l2_len;
    this.l3_len = l3_len;
    this.l4_type = l4_type.getValue();
  }

  public String getType() {
    return type;
  }

  public int getL2_len() {
    return l2_len;
  }

  public int getL3_len() {
    return l3_len;
  }

  public int getL4_type() {
    return l4_type;
  }
}
