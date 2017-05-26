package com.cisco.trex.stateless;

import java.util.Map;

public class TRexCommand {
    private String apiMethod;
    private Map<String, Object> parameters;

    public TRexCommand(String apiMethod, Map<String, Object> parameters) {
        this.apiMethod = apiMethod;
        this.parameters = parameters;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
