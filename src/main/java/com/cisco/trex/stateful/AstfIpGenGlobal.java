package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

/**
 * Astf Ip Gen Global
 */
public class AstfIpGenGlobal {
    private static final String DEFAULT_IP_OFFSET = "1.0.0.0";

    private String ipOffset;
    private JsonObject fields = new JsonObject();

    /**
     * construct
     *
     * @param ipOffset
     */
    public AstfIpGenGlobal(String ipOffset) {
        this.ipOffset = ipOffset;
        this.fields.addProperty("ip_offset", ipOffset);
    }

    /**
     * default construct by using default ipOffset="1.0.0.0"
     */
    public AstfIpGenGlobal() {
        this(DEFAULT_IP_OFFSET);
    }

    /**
     * to json format
     *
     * @return
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
