package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UtilizationCPU {
    @JsonProperty("history")
    private List<Integer> history;
    @JsonProperty("ports")
    private List<Integer> ports;

    @JsonProperty("history")
    public List<Integer> getHistory() {
        return history;
    }

    @JsonProperty("history")
    public void setHistory(final List<Integer> history) {
        this.history = history;
    }

    @JsonProperty("ports")
    public List<Integer> getPorts() {
        return ports;
    }

    @JsonProperty("ports")
    public void setPorts(final List<Integer> ports) {
        this.ports = ports;
    }
}
