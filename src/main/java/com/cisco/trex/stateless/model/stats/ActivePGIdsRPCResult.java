package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivePGIdsRPCResult {
  @JsonProperty("ids")
  private ActivePGIds ids = new ActivePGIds();

  @JsonProperty("ids")
  public ActivePGIds getIds() {
    return ids;
  }

  @JsonProperty("ids")
  public void setIds(final ActivePGIds ids) {
    this.ids = ids;
  }
}
