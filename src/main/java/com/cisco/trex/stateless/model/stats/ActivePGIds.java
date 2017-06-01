package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivePGIds {
    @JsonProperty("flow_stats")
    private int[] flowStats = new int[0];
    @JsonProperty("latency")
    private int[] latency = new int[0];

    @JsonProperty("flow_stats")
    public int[] getFlowStats() {
        return flowStats;
    }

    @JsonProperty("flow_stats")
    public void setFlowStats(final int[] flowStats) {
        this.flowStats = flowStats;
    }

    @JsonProperty("latency")
    public int[] getLatency() {
        return latency;
    }

    @JsonProperty("latency")
    public void setLatency(final int[] latency) {
        this.latency = latency;
    }
}
