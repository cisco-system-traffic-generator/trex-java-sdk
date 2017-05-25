package com.cisco.trex.stateless.model.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortFCAttribute {
    
    @JsonProperty("mode")
    private int mode;

    @JsonProperty("mode")
    public int getMode() {
        return mode;
    }

    @JsonProperty("mode")
    public void setMode(int mode) {
        this.mode = mode;
    }
}
