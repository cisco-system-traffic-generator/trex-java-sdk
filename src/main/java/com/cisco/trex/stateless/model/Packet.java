package com.cisco.trex.stateless.model;

import java.util.Base64;

public class Packet {
    /**
     * Binary representation encoded as base64 string
     */
    private String binary;
    private String meta = "";

    public Packet(String binaryBase64) {
        this(binaryBase64, "");
    }

    public Packet(String binaryBase64, String meta) {
        binary = binaryBase64;
        this.meta = meta;
    }


    public String getBinary() {
        return binary;
    }

    public byte[] getBytes() {
        return Base64.getDecoder().decode(binary.getBytes());
    }

    public String getMeta() {
        return meta;
    }
}
