package com.cisco.trex.stateful;

import org.apache.commons.lang3.StringUtils;

/**
 * Astf Tcp Info
 */
class AstfTcpInfo {
    private int port;

    /**
     * construct
     *
     * @param filePath
     */
    AstfTcpInfo(String filePath) {
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
