package com.cisco.trex.stateless.model.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortLinkAttribute {

  @JsonProperty("up")
  private boolean isUp;

  @JsonProperty("up")
  public boolean getUp() {
    return isUp;
  }

  @JsonProperty("up")
  public void setUp(boolean up) {
    isUp = up;
  }
}
