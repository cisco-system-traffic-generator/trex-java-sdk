package com.cisco.trex.stateful.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Meta data for ASTF statistics */
public class MetaData {

  @JsonProperty("data")
  public List<CounterMeta> data;

  @JsonProperty("name")
  public String name;

  @JsonProperty("data")
  public List<CounterMeta> getData() {
    return data;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }
}
