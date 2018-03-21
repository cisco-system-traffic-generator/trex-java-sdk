package com.cisco.trex.stateless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RPCResponse {
  @JsonProperty("id")
  private String id;

  @JsonProperty("jsonrpc")
  private String jsonrpc;

  @JsonProperty("result")
  private String result;

  @JsonProperty("error")
  private RPCResponseError error;

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(final String id) {
    this.id = id;
  }

  @JsonProperty("jsonrpc")
  public String getJsonrpc() {
    return jsonrpc;
  }

  @JsonProperty("jsonrpc")
  public void setJsonrpc(final String jsonrpc) {
    this.jsonrpc = jsonrpc;
  }

  @JsonProperty("result")
  public String getResult() {
    return result;
  }

  @JsonProperty("result")
  public void setResult(final JsonNode result) {
    this.result = result.toString();
  }

  @JsonProperty("error")
  public RPCResponseError getError() {
    return error;
  }

  @JsonProperty("error")
  public void setError(RPCResponseError error) {
    this.error = error;
  }

  public boolean isFailed() {
    return error != null;
  }
}
