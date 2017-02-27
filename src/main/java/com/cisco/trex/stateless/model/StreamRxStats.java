package com.cisco.trex.stateless.model;

public class StreamRxStats {
    private Boolean enabled;
    private Boolean latency_enabled;
    private Boolean seq_enabled;
    private Integer stream_id;

    public StreamRxStats(Boolean enabled, Boolean latency_enabled, Boolean seq_enabled, Integer stream_id) {
        this.enabled = enabled;
        this.latency_enabled = latency_enabled;
        this.seq_enabled = seq_enabled;
        this.stream_id = stream_id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getLatency_enabled() {
        return latency_enabled;
    }

    public Boolean getSeq_enabled() {
        return seq_enabled;
    }

    public Integer getStream_id() {
        return stream_id;
    }
}
