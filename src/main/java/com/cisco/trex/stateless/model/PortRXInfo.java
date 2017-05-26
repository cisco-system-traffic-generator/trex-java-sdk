package com.cisco.trex.stateless.model;

import com.cisco.trex.stateless.model.port.PortGARPAttribute;
import com.cisco.trex.stateless.model.port.PortLatencyAttribute;
import com.cisco.trex.stateless.model.port.PortQueueAttribute;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortRXInfo {
    @JsonProperty("grat_arp")
    private PortGARPAttribute gratArp;

    @JsonProperty("latency")
    private PortLatencyAttribute latency;

    @JsonProperty("queue")
    private PortQueueAttribute queue;

    @JsonProperty("grat_arp")
    public PortGARPAttribute getGratArp() {
        return gratArp;
    }

    @JsonProperty("grat_arp")
    public void setGratArp(PortGARPAttribute gratArp) {
        this.gratArp = gratArp;
    }

    @JsonProperty("latency")
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
