package com.cisco.trex.stateful.model;

import com.google.gson.annotations.SerializedName;

/** counters for flow info */
public class FlowInfoData {

  @SerializedName("duration")
  private double duration;

  @SerializedName("last_data_recv")
  private long lastDataRecv;

  @SerializedName("options")
  private String options;

  @SerializedName("origin")
  private String origin;

  @SerializedName("rcv_mss")
  private long rcvMss;

  @SerializedName("rcv_nxt")
  private long rcvNxt;

  @SerializedName("rcv_ooopack")
  private long rcvOoopack;

  @SerializedName("rcv_space")
  private long rcvSpace;

  @SerializedName("rcv_wscale")
  private short rcvWscale;

  @SerializedName("rto")
  private long rto;

  @SerializedName("rtt")
  private long rtt;

  @SerializedName("rttvar")
  private long rttvar;

  @SerializedName("snd_cwnd")
  private long sndCwnd;

  @SerializedName("snd_mss")
  private long sndMss;

  @SerializedName("snd_nxt")
  private long sndNxt;

  @SerializedName("snd_rexmitpack")
  private long sndRexmitpack;

  @SerializedName("snd_ssthresh")
  private long sndSsthresh;

  @SerializedName("snd_wnd")
  private long sndWnd;

  @SerializedName("snd_wscale")
  private short sndWscale;

  @SerializedName("snd_zerowin")
  private long sndZerowin;

  @SerializedName("state")
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
