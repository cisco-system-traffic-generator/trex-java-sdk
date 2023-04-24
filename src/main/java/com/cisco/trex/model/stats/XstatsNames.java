package com.cisco.trex.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

/** statistics for trex port */
public class XstatsNames {

  /** Json property */
  @JsonProperty("xstats_names")
  public String[] names;

  /** @return names */
  public String[] getAllNames() {
    return names;
  }
}
