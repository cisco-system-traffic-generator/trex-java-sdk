package com.cisco.trex.stateless.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobalStatistics {

  @JsonProperty("m_active_flows")
  private double mActiveFlows = 0.0;

  @JsonProperty("m_active_sockets")
  private long mActiveSockets = 0;

  @JsonProperty("m_bw_per_core")
  private double mBwPerCore = 0.0;

  @JsonProperty("m_cpu_util")
  private double mCpuUtil = 0.0;

  @JsonProperty("m_cpu_util_raw")
  private double mCpuUtilRaw = 0.0;

  @JsonProperty("m_open_flows")
  private double mOpenFlows = 0.0;

  @JsonProperty("m_platform_factor")
  private double mPlatformFactor = 0.0;

  @JsonProperty("m_rx_bps")
  private double mRxBps = 0.0;

  @JsonProperty("m_rx_core_pps")
  private double mRxCorePps = 0.0;

  @JsonProperty("m_rx_cpu_util")
  private double mRxCpuUtil = 0.0;

  @JsonProperty("m_rx_drop_bps")
  private double mRxDropBps = 0.0;

  @JsonProperty("m_rx_pps")
  private double mRxPps = 0.0;

  @JsonProperty("m_socket_util")
  private double mSocketUtil = 0.0;

  @JsonProperty("m_total_alloc_error")
  private long mTotalAllocError = 0;

  @JsonProperty("m_total_clients")
  private long mTotalClients = 0;

  @JsonProperty("m_total_nat_active ")
  private long mTotalNatActive = 0;

  @JsonProperty("m_total_nat_learn_error")
  private long mTotalNatLearnError = 0;

  @JsonProperty("m_total_nat_no_fid ")
  private long mTotalNatNoFid = 0;

  @JsonProperty("m_total_nat_open   ")
  private long mTotalNatOpen = 0;

  @JsonProperty("m_total_nat_syn_wait")
  private long mTotalNatSynWait = 0;

  @JsonProperty("m_total_nat_time_out")
  private long mTotalNatTimeOut = 0;

  @JsonProperty("m_total_nat_time_out_wait_ack")
  private long mTotalNatTimeOutWaitAck = 0;

  @JsonProperty("m_total_queue_drop")
  private long mTotalQueueDrop = 0;

  @JsonProperty("m_total_queue_full")
  private long mTotalQueueFull = 0;

  @JsonProperty("m_total_rx_bytes")
  private long mTotalRxBytes = 0;

  @JsonProperty("m_total_rx_pkts")
  private long mTotalRxPkts = 0;

  @JsonProperty("m_total_servers")
  private long mTotalServers = 0;

  @JsonProperty("m_total_tx_bytes")
  private long mTotalTxBytes = 0;

  @JsonProperty("m_total_tx_pkts")
  private long mTotalTxPkts = 0;

  @JsonProperty("m_tx_bps")
  private double mTxBps = 0.0;

  @JsonProperty("m_tx_cps")
  private double mTxCps = 0.0;

  @JsonProperty("m_tx_expected_bps")
  private double mTxExpectedBps = 0.0;

  @JsonProperty("m_tx_expected_cps")
  private double mTxExpectedCps = 0.0;

  @JsonProperty("m_tx_expected_pps")
  private double mTxExpectedPps = 0.0;

  @JsonProperty("m_tx_pps")
  private double mTxPps = 0.0;

  @JsonProperty("m_active_flows")
  public double getMActiveFlows() {
    return mActiveFlows;
  }

  @JsonProperty("m_active_sockets")
  public long getMActiveSockets() {
    return mActiveSockets;
  }

  @JsonProperty("m_bw_per_core")
  public double getMBwPerCore() {
    return mBwPerCore;
  }

  @JsonProperty("m_cpu_util")
  public double getMCpuUtil() {
    return mCpuUtil;
  }

  @JsonProperty("m_cpu_util_raw")
  public double getMCpuUtilRaw() {
    return mCpuUtilRaw;
  }

  @JsonProperty("m_open_flows")
  public double getMOpenFlows() {
    return mOpenFlows;
  }

  @JsonProperty("m_platform_factor")
  public double getMPlatformFactor() {
    return mPlatformFactor;
  }

  @JsonProperty("m_rx_bps")
  public double getMRxBps() {
    return mRxBps;
  }

  @JsonProperty("m_rx_core_pps")
  public double getMRxCorePps() {
    return mRxCorePps;
  }

  @JsonProperty("m_rx_cpu_util")
  public double getMRxCpuUtil() {
    return mRxCpuUtil;
  }

  @JsonProperty("m_rx_drop_bps")
  public double getMRxDropBps() {
    return mRxDropBps;
  }

  @JsonProperty("m_rx_pps")
  public Double getMRxPps() {
    return mRxPps;
  }

  @JsonProperty("m_socket_util")
  public double getMSocketUtil() {
    return mSocketUtil;
  }

  @JsonProperty("m_total_alloc_error")
  public long getMTotalAllocError() {
    return mTotalAllocError;
  }

  @JsonProperty("m_total_clients")
  public long getMTotalClients() {
    return mTotalClients;
  }

  @JsonProperty("m_total_nat_active ")
  public long getMTotalNatActive() {
    return mTotalNatActive;
  }

  @JsonProperty("m_total_nat_learn_error")
  public long getMTotalNatLearnError() {
    return mTotalNatLearnError;
  }

  @JsonProperty("m_total_nat_no_fid ")
  public long getMTotalNatNoFid() {
    return mTotalNatNoFid;
  }

  @JsonProperty("m_total_nat_open ")
  public long getMTotalNatOpen() {
    return mTotalNatOpen;
  }

  @JsonProperty("m_total_nat_syn_wait")
  public long getMTotalNatSynWait() {
    return mTotalNatSynWait;
  }

  @JsonProperty("m_total_nat_time_out")
  public long getMTotalNatTimeOut() {
    return mTotalNatTimeOut;
  }

  @JsonProperty("m_total_nat_time_out_wait_ack")
  public long getMTotalNatTimeOutWaitAck() {
    return mTotalNatTimeOutWaitAck;
  }

  @JsonProperty("m_total_queue_drop")
  public long getMTotalQueueDrop() {
    return mTotalQueueDrop;
  }

  @JsonProperty("m_total_queue_full")
  public long getMTotalQueueFull() {
    return mTotalQueueFull;
  }

  @JsonProperty("m_total_rx_bytes")
  public long getMTotalRxBytes() {
    return mTotalRxBytes;
  }

  @JsonProperty("m_total_rx_pkts")
  public long getMTotalRxPkts() {
    return mTotalRxPkts;
  }

  @JsonProperty("m_total_servers")
  public long getMTotalServers() {
    return mTotalServers;
  }

  @JsonProperty("m_total_tx_bytes")
  public long getMTotalTxBytes() {
    return mTotalTxBytes;
  }

  @JsonProperty("m_total_tx_pkts")
  public long getMTotalTxPkts() {
    return mTotalTxPkts;
  }

  @JsonProperty("m_tx_bps")
  public double getMTxBps() {
    return mTxBps;
  }

  @JsonProperty("m_tx_cps")
  public double getMTxCps() {
    return mTxCps;
  }

  @JsonProperty("m_tx_expected_bps")
  public double getMTxExpectedBps() {
    return mTxExpectedBps;
  }

  @JsonProperty("m_tx_expected_cps")
  public double getMTxExpectedCps() {
    return mTxExpectedCps;
  }

  @JsonProperty("m_tx_expected_pps")
  public double getMTxExpectedPps() {
    return mTxExpectedPps;
  }

  @JsonProperty("m_tx_pps")
  public double getMTxPps() {
    return mTxPps;
  }
}
