package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiVersion {

  @JsonProperty("api_vers")
  private ApiVersionHolder[] apiVers;

  @JsonProperty("api_vers")
  public ApiVersionHolder[] getApiVers() {
    return apiVers;
  }

  @JsonProperty("api_vers")
  public void setApiVers(ApiVersionHolder[] apiVers) {
    this.apiVers = apiVers;
  }

  public String getType() {
    return apiVers[0].getType();
  }

  public String getApiH() {
    return apiVers[0].getApiH();
  }
}
