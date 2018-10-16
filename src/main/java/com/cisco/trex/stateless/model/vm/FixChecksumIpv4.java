package com.cisco.trex.stateless.model.vm;

public class FixChecksumIpv4 extends VMInstruction {

    private String type;
    private int pkt_offset;

    public FixChecksumIpv4(int pkt_offset) {
        super();
        this.type = "fix_checksum_ipv4";
        this.pkt_offset = pkt_offset;
    }

    public String getType() {
        return type;
    }

    public int getPkt_offset() {
        return pkt_offset;
    }

}
