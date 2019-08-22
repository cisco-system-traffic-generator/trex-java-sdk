package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LayerConfigurationMode {
  @JsonProperty("ether")
  L2Configuration l2Configuration;

  @JsonProperty("ipv4")
  L3Configuration l3Configuration;

  @JsonProperty("ether")
  public L2Configuration getL2Configuration() {
    return l2Configuration;
  }

  @JsonProperty("ether")
  public void setL2Configuration(L2Configuration l2Configuration) {
    this.l2Configuration = l2Configuration;
  }

  @JsonProperty("ipv4")
  public L3Configuration getL3Configuration() {
    return l3Configuration;
  }

  @JsonProperty("ipv4")
  public void setL3Configuration(L3Configuration l3Configuration) {
    this.l3Configuration = l3Configuration;
  }
}
