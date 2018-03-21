package com.cisco.trex.stateless.model;

import com.cisco.trex.stateless.model.port.PortAttributes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortStatus {

  @JsonProperty("attr")
  public PortAttributes attr;

  @JsonProperty("max_stream_id")
  private int maxStreamId;

  @JsonProperty("owner")
  public String owner;

  @JsonProperty("rx_info")
  private PortRXInfo rxInfo;

  @JsonProperty("service")
  public Boolean service;

  @JsonProperty("state")
  public String state;

  @JsonProperty("attr")
  public PortAttributes getAttr() {
    return attr;
  }

  @JsonProperty("attr")
  public void setAttr(PortAttributes attr) {
    this.attr = attr;
  }

  @JsonProperty("max_stream_id")
  public int getMaxStreamId() {
    return maxStreamId;
  }

  @JsonProperty("max_stream_id")
  public void setMaxStreamId(int maxStreamId) {
    this.maxStreamId = maxStreamId;
  }

  @JsonProperty("owner")
  public String getOwner() {
    return owner;
  }

  @JsonProperty("owner")
  public void setOwner(String owner) {
    this.owner = owner;
  }

  @JsonProperty("rx_info")
  public PortRXInfo getRxInfo() {
    return rxInfo;
  }

  @JsonProperty("rx_info")
  public void setRxInfo(PortRXInfo rxInfo) {
    this.rxInfo = rxInfo;
  }

  @JsonProperty("service")
  public Boolean getService() {
    return service;
  }

  @JsonProperty("service")
  public void setService(Boolean service) {
    this.service = service;
  }

  @JsonProperty("state")
  public String getState() {
    return state;
  }

  @JsonProperty("state")
  public void setState(String state) {
    this.state = state;
  }
}
