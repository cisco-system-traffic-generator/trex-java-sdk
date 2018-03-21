package com.cisco.trex.stateless.model.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortMulticastAttribute {

  @JsonProperty("multicast")
  private boolean enabled;

  @JsonProperty("multicast")
  public boolean getEnabled() {
    return enabled;
  }

  @JsonProperty("multicast")
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
