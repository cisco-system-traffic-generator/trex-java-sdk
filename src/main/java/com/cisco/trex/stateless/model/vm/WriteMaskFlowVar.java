package com.cisco.trex.stateless.model.vm;

public class WriteMaskFlowVar extends VMInstruction {

    private String type;
    private String name;
    private int pkt_offset;
    private int add_value;
    private int pkt_cast_size;
    private long mask;
    private int shift;
    private boolean is_big_endian;

    public WriteMaskFlowVar(String name, int pkt_offset, int add_value, int pkt_cast_size, long mask, int shift,
            boolean is_big_endian) {
        super();
        this.type = "write_mask_flow_var";
        this.name = name;
        this.pkt_offset = pkt_offset;
        this.add_value = add_value;
        this.pkt_cast_size = pkt_cast_size;
        this.mask = mask;
        this.shift = shift;
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

    public int getPkt_cast_size() {
        return pkt_cast_size;
    }

    public long getMask() {
        return mask;
    }

    public int getShift() {
        return shift;
    }

    public boolean getIs_big_endian() {
        return is_big_endian;
    }

}
