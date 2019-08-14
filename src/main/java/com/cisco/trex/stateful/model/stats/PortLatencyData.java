package com.cisco.trex.stateful.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PortLatencyData {

    @JsonProperty("hist")
    public PortHistData hist;

    @JsonProperty("stats")
    public PortStatsData stats;

    public PortHistData getHist() {
        return hist;
    }

    public void setHist(PortHistData hist) {
        this.hist = hist;
    }

    public PortStatsData getStats() {
        return stats;
    }

    public void setStats(PortStatsData stats) {
        this.stats = stats;
    }
}
