package com.cisco.trex.stateful;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiAstfVersion {

    @JsonProperty("api_h")
    private String apiH;

    public ApiAstfVersion() {}

    @JsonProperty("api_h")
    public String getApiH() {
        return this.apiH;
    }

    @JsonProperty("api_h")
    public void setApiH(String apiH) {
        this.apiH = apiH;
    }

}
