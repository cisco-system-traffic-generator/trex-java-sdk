package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class L2Configuration {
  @JsonProperty("dst")
  private String dst;

  @JsonProperty("src")
  private String src;

  @JsonProperty("status")
  private String state;

  @JsonProperty("dst")
  public String getDst() {
    return dst;
  }

  @JsonProperty("dst")
  public void setDst(String dst) {
    this.dst = dst;
  }

  @JsonProperty("src")
  public String getSrc() {
    return src;
  }

  @JsonProperty("src")
  public void setSrc(String src) {
    this.src = src;
  }

  @JsonProperty("status")
  public String getStatus() {
    return state;
  }

  @JsonProperty("status")
  public void setStatus(String status) {
    this.state = status;
  }
}
