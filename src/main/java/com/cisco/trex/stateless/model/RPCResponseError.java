package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RPCResponseError {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("specific_err")
    private String specificErr;

    @JsonProperty("code")
    public Integer getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(Integer code) {
        this.code = code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("specific_err")
    public String getSpecificErr() {
        return specificErr;
    }

    @JsonProperty("specific_err")
    public void setSpecificErr(String specificErr) {
        this.specificErr = specificErr;
    }
}
