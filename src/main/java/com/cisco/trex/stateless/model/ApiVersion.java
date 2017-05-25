package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiVersion {

    @JsonProperty("api_vers")
    private ApiVers[] api_vers;

    public ApiVers[] getApi_vers() {
        return api_vers;
    }

    public void setApi_vers(ApiVers[] api_vers) {
        this.api_vers = api_vers;
    }

    public String getType() {
        return api_vers[0].getType();
    }
    
    public String getApi_h() {
        return api_vers[0].getApi_h();
    }
    
}
