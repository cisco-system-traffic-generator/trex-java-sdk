package com.cisco.trex.stateful.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class LatencyData {

    @JsonProperty("cpu_util")
    public String cpu_util;

    @JsonProperty("epoch")
    public Integer epoch;

    public Map<Integer, PortLatencyData> portLatencyDataMap;

    @JsonProperty("unknown")
    public Integer unknown;

    public String getCpu_util() {
        return cpu_util;
    }

    public void setCpu_util(String cpu_util) {
        this.cpu_util = cpu_util;
    }

    public Integer getEpoch() {
        return epoch;
    }

    public void setEpoch(Integer epoch) {
        this.epoch = epoch;
    }

    public Map<Integer, PortLatencyData> getPortLatencyDataMap() {
        return portLatencyDataMap;
    }

    public void setPortLatencyDataMap(Map<Integer, PortLatencyData> portLatencyDataMap) {
        this.portLatencyDataMap = portLatencyDataMap;
    }

    public Integer getUnknown() {
        return unknown;
    }

    public void setUnknown(Integer unknown) {
        this.unknown = unknown;
    }
}
