package com.cisco.trex.stateless.model.vm;

public class FixChecksumHw extends VMInstruction {

    private String type;
    private int l2_len;
    private int l3_len;
    private int l4_type;

    public FixChecksumHw(int l2_len, int l3_len, int l4_type) {
        super();
        this.type = "fix_checksum_hw";
        this.l2_len = l2_len;
        this.l3_len = l3_len;
        this.l4_type = l4_type;
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
