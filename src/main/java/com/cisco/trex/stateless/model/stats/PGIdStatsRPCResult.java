package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PGIdStatsRPCResult {
  @JsonProperty("flow_stats")
  private Map<String, FlowStat> flowStats = new HashMap<>();

  @JsonProperty("latency")
  private Map<String, LatencyStat> latency = new HashMap<>();

  @JsonProperty("ver_id")
  private Map<String, Integer> verId = new HashMap<>();

  @JsonProperty("flow_stats")
  public Map<String, FlowStat> getFlowStats() {
    return flowStats;
  }

  @JsonProperty("flow_stats")
  public void setFlowStats(final Map<String, FlowStat> flowStats) {
    this.flowStats = flowStats;
  }

  @JsonProperty("latency")
  public Map<String, LatencyStat> getLatency() {
    return latency;
  }

  @JsonProperty("latency")
  public void setLatency(final Map<String, LatencyStat> latency) {
    this.latency = latency;
  }

  @JsonProperty("ver_id")
  public Map<String, Integer> getVerId() {
    return verId;
  }

  @JsonProperty("ver_id")
  public void setVerId(final Map<String, Integer> verId) {
    this.verId = verId;
  }
}
