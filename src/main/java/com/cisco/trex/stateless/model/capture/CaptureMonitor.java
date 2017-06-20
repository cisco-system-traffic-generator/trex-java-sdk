package com.cisco.trex.stateless.model.capture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptureMonitor {
    
    @JsonProperty("capture_id")
    private int captureId;

    @JsonProperty("start_ts")
    private long startTs;

    @JsonProperty("capture_id")
    public int getcaptureId() {
        return captureId;
    }

    @JsonProperty("capture_id")
    public void setcaptureId(int captureId) {
        this.captureId = captureId;
    }
}
