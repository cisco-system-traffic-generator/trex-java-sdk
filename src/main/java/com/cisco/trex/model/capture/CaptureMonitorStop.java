package com.cisco.trex.model.capture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptureMonitorStop {
  @JsonProperty("pkt_count")
  private int pktCount;

  @JsonProperty("pkt_count")
  public int getPktCount() {
    return pktCount;
  }

  @JsonProperty("pkt_count")
  public void setPktCount(int pktCount) {
    this.pktCount = pktCount;
  }
}
