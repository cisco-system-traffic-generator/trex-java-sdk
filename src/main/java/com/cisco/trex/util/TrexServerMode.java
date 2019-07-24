package com.cisco.trex.util;

/**
 * Class represent for Trex Server Mode
 */
public enum TrexServerMode {
    ASTF("ASTF"),
    STL("STL"),
    UNKNOWN("UNKNOWN");

    private String serverMode;

    TrexServerMode(String serverMode) {
        this.serverMode = serverMode;
    }

    public String getServerMode() {
        return this.serverMode;
    }

}
