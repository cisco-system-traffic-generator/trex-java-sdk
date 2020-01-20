package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PortInfo {

    @JsonProperty(value = "ip")
    private String ip;

    @JsonProperty(value = "default_gw")
    private String defaultGw;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setDefaultGw(String defaultGw) {
        this.defaultGw = defaultGw;
    }

    public String getDefaultGw() {
        return defaultGw;
    }
}
