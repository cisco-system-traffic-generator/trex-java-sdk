package com.cisco.trex.stateful;

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;

/**
 * Astf Association Rule
 */
public class AstfAssociationRule {
    private String ipStart;
    private String ipEnd;
    private JsonObject fields = new JsonObject();
    private int port;

    /**
     * construct
     *
     * @param ipStart
     * @param ipEnd
     * @param port
     */
    public AstfAssociationRule(String ipStart, String ipEnd, int port) {
        this.port = port;
        this.ipStart = ipStart;
        this.ipEnd = ipEnd;
        fields.addProperty("port", port);
        if (!StringUtils.isEmpty(ipStart)) {
            fields.addProperty("ip_start", ipStart);
        }
        if (!StringUtils.isEmpty(ipStart)) {
            fields.addProperty("ip_end", ipEnd);
        }
    }

    /**
     * construct
     *
     * @param port
     */
    public AstfAssociationRule(int port) {
        this(null, null, port);
    }

    /**
     * get port
     *
     * @return port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * to json format
     *
     * @return JsonObject
     */
    public JsonObject toJson() {
        return fields;
    }
}
