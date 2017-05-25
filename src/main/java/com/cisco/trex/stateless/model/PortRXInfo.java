package com.cisco.trex.stateless.model;

import com.cisco.trex.stateless.model.port.PortGARPAttribute;
import com.cisco.trex.stateless.model.port.PortLatencyAttribute;
import com.cisco.trex.stateless.model.port.PortQueueAttribute;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortRXInfo {
    @JsonProperty("grat_arp")
    private PortGARPAttribute grat_arp;

    @JsonProperty("latency")
    private PortLatencyAttribute latency;

    @JsonProperty("queue")
    private PortQueueAttribute queue;

    @JsonProperty("grat_arp")
    public PortGARPAttribute getGrat_arp() {
        return grat_arp;
    }

    @JsonProperty("grat_arp")
    public void setGrat_arp(PortGARPAttribute grat_arp) {
        this.grat_arp = grat_arp;
    }

    @JsonProperty(" latency")
    public PortLatencyAttribute getLatency() {
        return latency;
    }

    @JsonProperty("latency")
    public void setLatency(PortLatencyAttribute latency) {
        this.latency = latency;
    }

    @JsonProperty("queue")
    public PortQueueAttribute getQueue() {
        return queue;
    }

    @JsonProperty("queue")
    public void setQueue(PortQueueAttribute queue) {
        this.queue = queue;
    }
}
