package com.cisco.trex.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

/** statistics for trex port */
public class PortStatistics {

  @JsonProperty("m_total_tx_bps")
  private double txBps = 0.0;

  @JsonProperty("m_total_rx_bps")
  private double rxBps = 0.0;

  @JsonProperty("m_total_tx_pps")
  private double txPps = 0.0;

  @JsonProperty("m_total_rx_pps")
  private double rxPps = 0;

  @JsonProperty("opackets")
  private long totalTxPackets = 0;

  @JsonProperty("ipackets")
  private long totalRxPackets = 0;

  @JsonProperty("obytes")
  private long totalTxBytes = 0;

  @JsonProperty("ibytes")
  private long totalRxBytes = 0;

  @JsonProperty("ierrors")
  private long rxErrors = 0;

  @JsonProperty("oerrors")
  private long txErrors = 0;

  @JsonProperty("m_cpu_util")
  private long mCpuUtil = 0;

  /** @return tx bps */
  public double getTxBps() {
    return txBps;
  }

  /** @return rx bps */
  public double getRxBps() {
    return rxBps;
  }

  /** @return tx pps */
  public double getTxPps() {
    return txPps;
  }

  /** @return rx pps */
  public double getRxPps() {
    return rxPps;
  }

  /** @return total tx packets */
  public long getTotalTxPackets() {
    return totalTxPackets;
  }

  /** @return total rx packets */
  public long getTotalRxPackets() {
    return totalRxPackets;
  }

  /** @return total tx bytes */
  public long getTotalTxBytes() {
    return totalTxBytes;
  }

  /** @return total rx bytes */
  public long getTotalRxBytes() {
    return totalRxBytes;
  }

  /** @return errors */
  public long getErrors() {
    return rxErrors + txErrors;
  }

  /** @return rx errors */
  public long getRxErrors() {
    return rxErrors;
  }

  /** @return tx errors */
  public long getTxErrors() {
    return txErrors;
  }

  /** @return mcpu util */
  public long getmCpuUtil() {
    return mCpuUtil;
  }
}
