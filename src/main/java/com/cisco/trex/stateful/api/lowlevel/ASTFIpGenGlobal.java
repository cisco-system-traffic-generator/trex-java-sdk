package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk ASTFIpGenGlobal class
 */
public class ASTFIpGenGlobal {
    private static final String DEFAULT_IP_OFFSET = "1.0.0.0";

    private String ipOffset;
    private JsonObject fields = new JsonObject();

    /**
     * construct
     *
     * @param ipOffset
     */
    public ASTFIpGenGlobal(String ipOffset) {
        this.ipOffset = ipOffset;
        this.fields.addProperty("ip_offset", ipOffset);
    }

    /**
     * default construct by using default ipOffset="1.0.0.0"
     */
    public ASTFIpGenGlobal() {
        this(DEFAULT_IP_OFFSET);
    }

    /**
     * to json format
     *
     * @return json
     */
    public JsonObject toJson() {
        return fields;
    }

    /**
     * getIpOffset
     *
     * @return ipOffset
     */
    public String getIpOffset() {
        return ipOffset;
    }
}
