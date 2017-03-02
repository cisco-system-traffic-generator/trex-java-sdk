package com.cisco.trex.stateless.model;

import com.google.gson.JsonObject;

public class LayerConfigurationMode {
    JsonObject attrs;

    public LayerConfigurationMode(JsonObject attrs) {
        this.attrs = attrs;
    }
    
    public String getEtherAttr(String attrName) {
        JsonObject ether = attrs.get("ether").getAsJsonObject();
        return ether != null ? ether.get(attrName).getAsString() : null;
    }
    
    public String getIpv4Attr(String attrName) {
        JsonObject ipV4 = attrs.get("ipv4").getAsJsonObject();
        return ipV4 != null ? ipV4.get(attrName).getAsString() : null;
    }
}
