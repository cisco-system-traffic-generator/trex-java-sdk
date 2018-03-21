package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiVersionHolder {
  @JsonProperty("type")
  private String type;

  @JsonProperty("api_h")
  private String apiH;

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("api_h")
  public String getApiH() {
    return apiH;
  }

  @JsonProperty("api_h")
  public void setApiH(String apiH) {
    this.apiH = apiH;
  }
}
