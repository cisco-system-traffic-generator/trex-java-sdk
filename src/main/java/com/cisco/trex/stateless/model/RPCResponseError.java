package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RPCResponseError {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("specific_err")
    private String specificErr;

    /**
     *
     * @return The code
     */
    @JsonProperty("code")
    public Integer getCode() {
        return code;
    }

    /**
     *
     * @param code The code
     */
    @JsonProperty("code")
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     *
     * @return The message
     */
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message The message
     */
    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return The specificErr
     */
    @JsonProperty("specific_err")
    public String getSpecificErr() {
        return specificErr;
    }

    /**
     *
     * @param specificErr The specific_err
     */
    @JsonProperty("specific_err")
    public void setSpecificErr(String specificErr) {
        this.specificErr = specificErr;
    }

}