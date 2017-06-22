package com.cisco.trex.stateless.model.capture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CapturedPkt {
    
    @JsonProperty("index")
    private int index;
    
    @JsonProperty("port")
    private int port;
    
    @JsonProperty("origin")
    private String origin;
    
    @JsonProperty("binary")
    private String binary;
    
    @JsonProperty("ts")
    private double timeStamp;

    @JsonProperty("index")
    public int getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(int index) {
        this.index = index;
    }

    @JsonProperty("port")
    public int getPort() {
        return port;
    }

    @JsonProperty("port")
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty("origin")
    public String getOrigin() {
        return origin;
    }

    @JsonProperty("origin")
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @JsonProperty("binary")
    public String getBinary() {
        return binary;
    }

    @JsonProperty("binary")
    public void setBinary(String binary) {
        this.binary = binary;
    }

    @JsonProperty("ts")
    public double getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty("ts")
    public void setTimeStamp(double timeStamp) {
        this.timeStamp = timeStamp;
    }
}
