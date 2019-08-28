package com.cisco.trex.stateful.api.lowlevel;

import org.apache.commons.lang3.StringUtils;

/** Java implementation for TRex python sdk _ASTFTCPInfo class */
class ASTFTCPInfo {
  private int port;

  /**
   * construct
   *
   * @param filePath
   */
  ASTFTCPInfo(String filePath) {
    if (StringUtils.isEmpty(filePath)) {
      CpcapReader cap = CapHandling.cpcapReader(filePath);
      cap.analyze();
      this.port = cap.getDstPort();
    }
  }

  /**
   * getPort
   *
   * @return port
   */
  public int getPort() {
    return port;
  }
}
