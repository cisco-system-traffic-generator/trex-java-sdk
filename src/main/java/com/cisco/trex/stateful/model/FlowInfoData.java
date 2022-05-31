package com.cisco.trex.stateful.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** counters for flow info */
public class FlowInfoData {

    @JsonProperty("duration")
    private double duration;

    @JsonProperty("last_data_recv")
    private long lastDataRecv;

    @JsonProperty("options")
    private String options;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("rcv_mss")
    private long rcvMss;

    @JsonProperty("rcv_nxt")
    private long rcvNxt;

    @JsonProperty("rcv_ooopack")
    private long rcvOoopack;

    @JsonProperty("rcv_space")
    private long rcvSpace;

    @JsonProperty("rcv_wscale")
    private short rcvWscale;

    @JsonProperty("rto")
    private long rto;

    @JsonProperty("rtt")
    private long rtt;

    @JsonProperty("rttvar")
    private long rttvar;

    @JsonProperty("snd_cwnd")
    private long sndCwnd;

    @JsonProperty("snd_mss")
    private long sndMss;

    @JsonProperty("snd_nxt")
    private long sndNxt;

    @JsonProperty("snd_rexmitpack")
    private long sndRexmitpack;

    @JsonProperty("snd_ssthresh")
    private long sndSsthresh;

    @JsonProperty("snd_wnd")
    private long sndWnd;

    @JsonProperty("snd_wscale")
    private short sndWscale;

    @JsonProperty("snd_zerowin")
    private long sndZerowin;

    @JsonProperty("state")
    private long state;

    public double getDuration() {
        return duration;
    }

    public long getLastDataRecv() {
        return lastDataRecv;
    }

    public String getOptions() {
        return options;
    }

    public String getOrigin() {
        return origin;
    }

    public long getRcvMss() {
        return rcvMss;
    }

    public long getRcvNxt() {
        return rcvNxt;
    }

    public long getRcvOoopack() {
        return rcvOoopack;
    }

    public long getRcvSpace() {
        return rcvSpace;
    }

    public short getRcvWscale() {
        return rcvWscale;
    }

    public long getRto() {
        return rto;
    }

    public long getRtt() {
        return rtt;
    }

    public long getRttvar() {
        return rttvar;
    }

    public long getSndCwnd() {
        return sndCwnd;
    }

    public long getSndMss() {
        return sndMss;
    }

    public long getSndNxt() {
        return sndNxt;
    }

    public long getSndRexmitpack() {
        return sndRexmitpack;
    }

    public long getSndSsthresh() {
        return sndSsthresh;
    }

    public long getSndWnd() {
        return sndWnd;
    }

    public short getSndWscale() {
        return sndWscale;
    }

    public long getSndZerowin() {
        return sndZerowin;
    }

    public long getState() {
        return state;
    }
}
