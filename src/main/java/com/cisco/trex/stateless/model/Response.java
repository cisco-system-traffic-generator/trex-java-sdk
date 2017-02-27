package com.cisco.trex.stateless.model;

import com.google.gson.JsonElement;

public class Response {
    private String id;
    private String jsonrpc;
    private JsonElement result;

    public Response(String id, String jsonrpc, JsonElement result) {
        this.id = id;
        this.jsonrpc = jsonrpc;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public JsonElement getResult() {
        return result;
    }
}
