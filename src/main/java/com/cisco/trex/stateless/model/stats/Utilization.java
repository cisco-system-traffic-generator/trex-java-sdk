package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Utilization {
  @JsonProperty("cpu")
  private List<UtilizationCPU> cpu = new ArrayList<>();

  @JsonProperty("mbuf_stats")
  private Map<String, Map<String, List<Integer>>> mbufStats = new HashMap<>();

  @JsonProperty("cpu")
  public List<UtilizationCPU> getCpu() {
    return cpu;
  }

  @JsonProperty("cpu")
  public void setCpu(final List<UtilizationCPU> cpu) {
    this.cpu = cpu;
  }

  @JsonProperty("mbuf_stats")
  public Map<String, Map<String, List<Integer>>> getMbufStats() {
    return mbufStats;
  }

  @JsonProperty("mbuf_stats")
  public void setMbufStats(final Map<String, Map<String, List<Integer>>> mbufStats) {
    this.mbufStats = mbufStats;
  }
}
