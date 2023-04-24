package com.cisco.trex.model.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IsActiveAttribute {

  @JsonProperty("is_active")
  private boolean isActive;

  @JsonProperty("is_active")
  public boolean getIsActive() {
    return isActive;
  }

  @JsonProperty("is_active")
  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }
}
