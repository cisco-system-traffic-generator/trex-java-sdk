package com.cisco.trex.stateful.model.stats;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LatencyPortHist {

    public static class Histogram{
        @SerializedName("key")
        public Integer key;

        @SerializedName("val")
        public Integer val;

        @SerializedName("key")
        public Integer getKey() {
            return key;
        }

        @SerializedName("key")
        public void setKey(Integer key) {
            this.key = key;
        }

        @SerializedName("val")
        public Integer getVal() {
            return val;
        }

        @SerializedName("val")
        public void setVal(Integer val) {
            this.val = val;
        }
    }

    @SerializedName("cnt")
    public Integer cnt;

    @SerializedName("high_cnt")
    public Integer highCnt;

    @SerializedName("histogram")
    public List<Histogram> histogram;

    @SerializedName("max_usec")
    public Integer maxUsec;

    @SerializedName("min_usec")
    public Integer minUsec;

    @SerializedName("s_avg")
    public Double sAvg;

    @SerializedName("s_max")
    public Double sMax;

    @SerializedName("cnt")
    public Integer getCnt() {
        return cnt;
    }

    @SerializedName("cnt")
    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    @SerializedName("high_cnt")
    public Integer getHighCnt() {
        return highCnt;
    }

    @SerializedName("high_cnt")
    public void setHighCnt(Integer highCnt) {
        this.highCnt = highCnt;
    }

    @SerializedName("histogram")
    public List<Histogram> getHistogram() {
        return histogram;
    }

    @SerializedName("histogram")
    public void setHistogram(List<Histogram> histogram) {
        this.histogram = histogram;
    }

    @SerializedName("max_usec")
    public Integer getMaxUsec() {
        return maxUsec;
    }

    @SerializedName("max_usec")
    public void setMaxUsec(Integer maxUsec) {
        this.maxUsec = maxUsec;
    }

    @SerializedName("min_usec")
    public Integer getMinUsec() {
        return minUsec;
    }

    @SerializedName("min_usec")
    public void setMinUsec(Integer minUsec) {
        this.minUsec = minUsec;
    }

    @SerializedName("s_avg")
    public Double getsAvg() {
        return sAvg;
    }

    @SerializedName("s_avg")
    public void setsAvg(Double sAvg) {
        this.sAvg = sAvg;
    }

    @SerializedName("s_max")
    public Double getsMax() {
        return sMax;
    }

    @SerializedName("s_max")
    public void setsMax(Double sMax) {
        this.sMax = sMax;
    }
}
