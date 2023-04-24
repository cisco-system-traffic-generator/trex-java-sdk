package com.cisco.trex.model.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortPromiscuousAttribute {

  @JsonProperty("enabled")
  private boolean enabled;

  @JsonProperty("enabled")
  public boolean getEnabled() {
    return enabled;
  }

  @JsonProperty("enabled")
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
