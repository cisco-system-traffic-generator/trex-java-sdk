package com.cisco.trex.model.capture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptureFilter {

  @JsonProperty("rx")
  private int rxPortMask;

  @JsonProperty("tx")
  private int txPortMask;

  @JsonProperty("bpf")
  private String bpfFilter;

  @JsonProperty("rx")
  public int getRxPortMask() {
    return rxPortMask;
  }

  @JsonProperty("rx")
  public void setRxPortMask(int rxPortMask) {
    this.rxPortMask = rxPortMask;
  }

  @JsonProperty("tx")
  public int getTxPortMask() {
    return txPortMask;
  }

  @JsonProperty("tx")
  public void setTxPortMask(int txPortMask) {
    this.txPortMask = txPortMask;
  }

  @JsonProperty("bpf")
  public String getBpfFilter() {
    return this.bpfFilter;
  }

  @JsonProperty("bpf")
  public void setBpfFilter(String bpfFilter) {
    this.bpfFilter = bpfFilter;
  }
}
