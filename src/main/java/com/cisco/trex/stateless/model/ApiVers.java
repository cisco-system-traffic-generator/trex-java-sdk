package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true) 
public class ApiVers {
    @JsonProperty("type")
    private String type;

    @JsonProperty("api_h")
    private String api_h;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApi_h() {
        return api_h;
    }

    public void setApi_h(String api_h) {
        this.api_h = api_h;
    }
}
