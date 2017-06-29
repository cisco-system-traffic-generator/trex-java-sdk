package com.cisco.trex.stateless.model.capture;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptureInfo {
    
    @JsonProperty("bytes")
    private int bytes;

    @JsonProperty("count")
    private int count;

    @JsonProperty("filter")
    private CaptureFilter filter;

    @JsonProperty("id")
    private int id;

    @JsonProperty("limit")
    private int limit;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("id")
    public int getId() {
        return id;
    }
    
    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }
    
    @JsonProperty("bytes")
    public int getBytes() {
        return bytes;
    }
    
    @JsonProperty("bytes")
    public void setBytes(int bytes) {
        this.bytes = bytes;
    }
    
    @JsonProperty("count")
    public int getCount() {
        return count;
    }
    
    @JsonProperty("count")
    public void setCount(int count) {
        this.count = count;
    }
    
    @JsonProperty("limit")
    public int getLimit() {
        return limit;
    }
    
    @JsonProperty("limit")
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    @JsonProperty("state")
    public String getState() {
        return state;
    }
    
    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }
    
    @JsonProperty("filter")
    public CaptureFilter getFilter() {
        return filter;
    }
    
    @JsonProperty("filter")
    public void setFilter(CaptureFilter filter) {
        this.filter = filter;
    }
}
