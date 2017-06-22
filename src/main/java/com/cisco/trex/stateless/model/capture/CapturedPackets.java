package com.cisco.trex.stateless.model.capture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CapturedPackets {
    
    @JsonProperty("pending")
    private int pendingPkts;
    
    @JsonProperty("start_ts")
    private double startTimeStamp;
    
    @JsonProperty("pkts")
    private CapturedPkt[] pkts;

    @JsonProperty("pending")
    public int getPendingPkts() {
        return pendingPkts;
    }

    @JsonProperty("pending")
    public void setPendingPkts(int pendingPkts) {
        this.pendingPkts = pendingPkts;
    }

    @JsonProperty("start_ts")
    public double getStartTimeStamp() {
        return startTimeStamp;
    }

    @JsonProperty("start_ts")
    public void setStartTimeStamp(double startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    @JsonProperty("pkts")
    public List<CapturedPkt> getPkts() {
        return Arrays.stream(pkts).collect(Collectors.toList());
    }

    @JsonProperty("pkts")
    public void setPkts(CapturedPkt[] pkts) {
        this.pkts = pkts;
    }
}
