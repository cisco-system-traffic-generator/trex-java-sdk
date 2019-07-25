package com.cisco.trex.util;

/**
 * Class represent for Trex Server Mode
 */
public enum TRexServerMode {
    ASTF("ASTF"),
    STL("STL"),
    UNKNOWN("UNKNOWN");

    private String serverMode;

    TRexServerMode(String serverMode) {
        this.serverMode = serverMode;
    }

    public String getServerMode() {
        return this.serverMode;
    }

}
