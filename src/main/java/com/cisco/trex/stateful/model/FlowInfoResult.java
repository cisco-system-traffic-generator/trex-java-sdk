package com.cisco.trex.stateful.model;

import java.math.BigInteger;
import java.util.Map;

/** Data returned from get_flow_info API */
public class FlowInfoResult {

  private BigInteger index;
  private Map<String, FlowInfoData> flowInfoDataMap;

  public FlowInfoResult(BigInteger index, Map<String, FlowInfoData> flowInfoDataMap) {
    this.index = index;
    this.flowInfoDataMap = flowInfoDataMap;
  }

  /**
   * Get base index in memory
   *
   * @return
   */
  public BigInteger getIndex() {
    return index;
  }

  /**
   * Get flow info data map, as format of <string to present traffic direction , a flowInfoData
   * object containsing flow info counters> ex., key is "1.1.1.1:41668-1.1.2.1:20" value is
   * flowInfoData object refer to {@link FlowInfoData}
   *
   * @return
   */
  public Map<String, FlowInfoData> getFlowInfoDataMap() {
    return flowInfoDataMap;
  }
}
