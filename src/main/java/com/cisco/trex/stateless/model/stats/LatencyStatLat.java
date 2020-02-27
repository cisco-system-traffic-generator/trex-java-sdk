package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStatLat {
  @JsonProperty("average")
  private double average = 0;

  @JsonProperty("histogram")
  private Map<String, Long> histogram = new HashMap<>();

  @JsonProperty("jit")
  private long jit = 0;

  @JsonProperty("last_max")
  private long lastMax = 0;

  @JsonProperty("total_max")
  private long totalMax = 0;

  @JsonProperty("total_min")
  private long totalMin = 0;

  @JsonProperty("average")
  public double getAverage() {
    return average;
  }

  @JsonProperty("average")
  public void setAverage(final double average) {
    this.average = average;
  }

  @JsonProperty("histogram")
  public Map<String, Long> getHistogram() {
    return histogram;
  }

  @JsonProperty("histogram")
  public void setHistogram(final Map<String, Long> histogram) {
    this.histogram = histogram;
  }

  @JsonProperty("jit")
  public long getJit() {
    return jit;
  }

  @JsonProperty("jit")
  public void setJit(final long jit) {
    this.jit = jit;
  }

  @JsonProperty("last_max")
  public long getLastMax() {
    return lastMax;
  }

  @JsonProperty("last_max")
  public void setLastMax(final long lastMax) {
    this.lastMax = lastMax;
  }

  @JsonProperty("total_max")
  public long getTotalMax() {
    return totalMax;
  }

  @JsonProperty("total_max")
  public void setTotalMax(final long totalMax) {
    this.totalMax = totalMax;
  }

  @JsonProperty("total_min")
  public long getTotalMin() {
    return totalMin;
  }

  @JsonProperty("total_min")
  public void setTotalMin(final long totalMin) {
    this.totalMin = totalMin;
  }
}
