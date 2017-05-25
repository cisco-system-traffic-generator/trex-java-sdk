package com.cisco.trex.stateless.model.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortLatencyAttribute {
    
    @JsonProperty("is_active")
    private boolean is_active;

    @JsonProperty("is_active")
    public boolean getIsActive() {
        return is_active;
    }

    @JsonProperty("is_active")
    public void setIsActive(boolean is_active) {
        this.is_active = is_active;
    }
}
