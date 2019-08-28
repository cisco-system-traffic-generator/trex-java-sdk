package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LatencyStat {
  @JsonProperty("er")
  private LatencyStatErr err = new LatencyStatErr();

  @JsonProperty("lat")
  private LatencyStatLat lat = new LatencyStatLat();

  @JsonProperty("er")
  public LatencyStatErr getErr() {
    return err;
  }

  @JsonProperty("er")
  public void setErr(final LatencyStatErr err) {
    this.err = err;
  }

  @JsonProperty("lat")
  public LatencyStatLat getLat() {
    return lat;
  }

  @JsonProperty("lat")
  public void setLat(final LatencyStatLat lat) {
    this.lat = lat;
  }
}
