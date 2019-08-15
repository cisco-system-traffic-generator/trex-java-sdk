package com.cisco.trex.stateful.model.stats;

import com.google.gson.annotations.SerializedName;

public class LatencyPortData {

    @SerializedName("hist")
    public LatencyPortHist hist;

    @SerializedName("stats")
    public LatencyPortStats stats;

    @SerializedName("hist")
    public LatencyPortHist getHist() {
        return hist;
    }

    @SerializedName("hist")
    public void setHist(LatencyPortHist hist) {
        this.hist = hist;
    }

    @SerializedName("stats")
    public LatencyPortStats getStats() {
        return stats;
    }

    @SerializedName("stats")
    public void setStats(LatencyPortStats stats) {
        this.stats = stats;
    }
}
