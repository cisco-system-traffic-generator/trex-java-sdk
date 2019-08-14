package com.cisco.trex.stateful.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HistogramData {

    @JsonProperty("key")
    public Integer key;

    @JsonProperty("val")
    public Integer val;

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

}
