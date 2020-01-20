package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Platform {

    @JsonProperty(value = "master_thread_id")
    private Integer masterThreadId;

    @JsonProperty(value = "latency_thread_id")
    private Integer latencyThreadId;

    @JsonProperty(value = "dual_if")
    private List<DualIf> dualIfs;

    public Integer getMasterThreadId() {
        return masterThreadId;
    }

    public void setMasterThreadId(Integer masterThreadId) {
        this.masterThreadId = masterThreadId;
    }

    public Integer getLatencyThreadId() {
        return latencyThreadId;
    }

    public void setLatencyThreadId(Integer latencyThreadId) {
        this.latencyThreadId = latencyThreadId;
    }

    public List<DualIf> getDualIfs() {
        return dualIfs;
    }

    public void setDualIfs(List<DualIf> dualIfs) {
        this.dualIfs = dualIfs;
    }
}
