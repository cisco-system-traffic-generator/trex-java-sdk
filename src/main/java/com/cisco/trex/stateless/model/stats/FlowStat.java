package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowStat {
    @JsonProperty("rb")
    private Map<String, Long> rb = new HashMap<>();
    @JsonProperty("rbs")
    private Map<String, Double> rbs = new HashMap<>();
    @JsonProperty("rp")
    private Map<String, Long> rp = new HashMap<>();
    @JsonProperty("rps")
    private Map<String, Double> rps = new HashMap<>();
    @JsonProperty("tb")
    private Map<String, Long> tb = new HashMap<>();
    @JsonProperty("tbs")
    private Map<String, Double> tbs = new HashMap<>();
    @JsonProperty("tp")
    private Map<String, Long> tp = new HashMap<>();
    @JsonProperty("tps")
    private Map<String, Double> tps = new HashMap<>();

    @JsonProperty("rb")
    public Map<String, Long> getRb() {
        return rb;
    }

    @JsonProperty("rb")
    public void setRb(final Map<String, Long> rb) {
        this.rb = rb;
    }

    @JsonProperty("rbs")
    public Map<String, Double> getRbs() {
        return rbs;
    }

    @JsonProperty("rbs")
    public void setRbs(final Map<String, Double> rbs) {
        this.rbs = rbs;
    }

    @JsonProperty("rp")
    public Map<String, Long> getRp() {
        return rp;
    }

    @JsonProperty("rp")
    public void setRp(final Map<String, Long> rp) {
        this.rp = rp;
    }

    @JsonProperty("rps")
    public Map<String, Double> getRps() {
        return rps;
    }

    @JsonProperty("rps")
    public void setRps(final Map<String, Double> rps) {
        this.rps = rps;
    }

    @JsonProperty("tb")
    public Map<String, Long> getTb() {
        return tb;
    }

    @JsonProperty("tb")
    public void setTb(final Map<String, Long> tb) {
        this.tb = tb;
    }

    @JsonProperty("tbs")
    public Map<String, Double> getTbs() {
        return tbs;
    }

    @JsonProperty("tbs")
    public void setTbs(final Map<String, Double> tbs) {
        this.tbs = tbs;
    }

    @JsonProperty("tp")
    public Map<String, Long> getTp() {
        return tp;
    }

    @JsonProperty("tp")
    public void setTp(final Map<String, Long> tp) {
        this.tp = tp;
    }

    @JsonProperty("tps")
    public Map<String, Double> getTps() {
        return tps;
    }

    @JsonProperty("tps")
    public void setTps(final Map<String, Double> tps) {
        this.tps = tps;
    }
}
