package com.cisco.trex.stateful.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PortHistData {

    @JsonProperty("cnt")
    public Integer cnt;

    @JsonProperty("high_cnt")
    public Integer high_cnt;

    @JsonProperty("histogram")
    public List<HistogramData> histogram;

    @JsonProperty("max_usec")
    public Integer max_usec;

    @JsonProperty("min_usec")
    public Integer min_usec;

    @JsonProperty("s_avg")
    public Double s_avg;

    @JsonProperty("s_max")
    public Double s_max;

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public Integer getHigh_cnt() {
        return high_cnt;
    }

    public void setHigh_cnt(Integer high_cnt) {
        this.high_cnt = high_cnt;
    }

    public List<HistogramData> getHistogram() {
        return histogram;
    }

    public void setHistogram(List<HistogramData> histogram) {
        this.histogram = histogram;
    }

    public Integer getMax_usec() {
        return max_usec;
    }

    public void setMax_usec(Integer max_usec) {
        this.max_usec = max_usec;
    }

    public Integer getMin_usec() {
        return min_usec;
    }

    public void setMin_usec(Integer min_usec) {
        this.min_usec = min_usec;
    }

    public Double getS_avg() {
        return s_avg;
    }

    public void setS_avg(Double s_avg) {
        this.s_avg = s_avg;
    }

    public Double getS_max() {
        return s_max;
    }

    public void setS_max(Double s_max) {
        this.s_max = s_max;
    }
}
