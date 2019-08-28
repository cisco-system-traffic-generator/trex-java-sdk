package com.cisco.trex.stateful.model.stats;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class LatencyData {

  @SerializedName("cpu_util")
  public String cpuUtil;

  @SerializedName("epoch")
  public Integer epoch;

  public Map<Integer, LatencyPortData> portLatencyDataMap;

  @SerializedName("unknown")
  public Integer unknown;

  @SerializedName("cpu_util")
  public String getCpuUtil() {
    return cpuUtil;
  }

  @SerializedName("cpu_util")
  public void setCpuUtil(String cpuUtil) {
    this.cpuUtil = cpuUtil;
  }

  @SerializedName("epoch")
  public Integer getEpoch() {
    return epoch;
  }

  @SerializedName("epoch")
  public void setEpoch(Integer epoch) {
    this.epoch = epoch;
  }

  public Map<Integer, LatencyPortData> getPortLatencyDataMap() {
    return portLatencyDataMap;
  }

  public void setPortLatencyDataMap(Map<Integer, LatencyPortData> portLatencyDataMap) {
    this.portLatencyDataMap = portLatencyDataMap;
  }

  @SerializedName("unknown")
  public Integer getUnknown() {
    return unknown;
  }

  @SerializedName("unknown")
  public void setUnknown(Integer unknown) {
    this.unknown = unknown;
  }
}
