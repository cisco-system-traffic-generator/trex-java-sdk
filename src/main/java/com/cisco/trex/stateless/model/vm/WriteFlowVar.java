package com.cisco.trex.stateless.model.vm;

public class WriteFlowVar extends VMInstruction {

  private String type;
  private String name;
  private int pkt_offset;
  private int add_value;
  private boolean is_big_endian;

  public WriteFlowVar(String name, int pkt_offset, int add_value, boolean is_big_endian) {
    super();
    this.type = "write_flow_var";
    this.name = name;
    this.pkt_offset = pkt_offset;
    this.add_value = add_value;
    this.is_big_endian = is_big_endian;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public int getPkt_offset() {
    return pkt_offset;
  }

  public int getAdd_value() {
    return add_value;
  }

  public Boolean getIs_big_endian() {
    return is_big_endian;
  }
}
