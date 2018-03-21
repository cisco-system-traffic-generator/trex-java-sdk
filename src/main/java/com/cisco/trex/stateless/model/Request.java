package com.cisco.trex.stateless.model;

import java.util.Map;

public class Request {

  private String methodName;

  private Map<String, Object> parameters;

  public Request(String methodName, Map<String, Object> parameters) {
    this.methodName = methodName;
    this.parameters = parameters;
  }
  //
  //    public JsonElement toJson() {
  //
  //        return
  //    }
}
