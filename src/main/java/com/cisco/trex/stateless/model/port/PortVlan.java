package com.cisco.trex.stateless.model.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortVlan {
  @JsonProperty("tags")
  public List<Integer> tags;

  @JsonProperty("tags")
  public List<Integer> getTags() {
    return tags;
  }

  @JsonProperty("tags")
  public void setTags(List<Integer> tags) {
    this.tags = tags;
  }
}
