package com.cisco.trex.stateful.model.stats;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Meta data for ASTF statistics
 */
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
