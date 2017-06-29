package com.cisco.trex.stateless.model.capture;

public enum CaptureActions {
    START("start"), STOP("stop");
    
    private String command;

    CaptureActions(String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return command;
    }
}
