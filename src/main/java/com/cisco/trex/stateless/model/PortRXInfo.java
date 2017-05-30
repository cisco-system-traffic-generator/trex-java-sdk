package com.cisco.trex.stateless.model;

import com.cisco.trex.stateless.model.port.IsActiveAttribute;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortRXInfo {
    @JsonProperty("grat_arp")
    private IsActiveAttribute gratArp;

    @JsonProperty("latency")
    private IsActiveAttribute latency;

    @JsonProperty("queue")
    private IsActiveAttribute queue;

    @JsonProperty("grat_arp")
    public IsActiveAttribute getGratArp() {
        return gratArp;
    }

    @JsonProperty("grat_arp")
    public void setGratArp(IsActiveAttribute gratArp) {
        this.gratArp = gratArp;
    }

    @JsonProperty("latency")
    public IsActiveAttribute getLatency() {
        return latency;
    }

    @JsonProperty("latency")
    public void setLatency(IsActiveAttribute latency) {
        this.latency = latency;
    }

    @JsonProperty("queue")
    public IsActiveAttribute getQueue() {
        return queue;
    }

    @JsonProperty("queue")
    public void setQueue(IsActiveAttribute queue) {
        this.queue = queue;
    }
}
