package com.cisco.trex.stateful.model.stats;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ASTF statistics
 */
public class AstfStatistics {

    /**
     * Json Property
     */
    @JsonProperty("client")
    private Map<Integer, Double> client = new HashMap<>();

    @JsonProperty("server")
    private Map<Integer, Double> server = new HashMap<>();

    @JsonProperty("epoch")
    private int epoch;

    @JsonProperty("name")
    private String name;

    private Map<String, Double> matchedNameAndValuesForClient = new HashMap<>();

    private Map<String, Double> matchedNameAndValuesForServer = new HashMap<>();

    @JsonProperty("client")
    public Map<Integer, Double> getClientCounters() {
        return client;
    }

    @JsonProperty("client")
    public void setClientCounters(Map<Integer, Double> clientCounters) {
        this.client = clientCounters;
    }

    @JsonProperty("server")
    public Map<Integer, Double> getServerCounters() {
        return server;
    }

    @JsonProperty("server")
    public void setServerCounters(Map<Integer, Double> serverCounters) {
        this.server = serverCounters;
    }

    @JsonProperty("epoch")
    public int getEpoch() {
        return epoch;
    }

    @JsonProperty("epoch")
    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param metaData
     * @return this
     */
    public AstfStatistics setCounterNames(MetaData metaData) {
        for (Map.Entry<Integer, Double> counter : client.entrySet()) {
            matchedNameAndValuesForClient.put(getCounterName(counter.getKey(), metaData), counter.getValue());
        }

        for (Map.Entry<Integer, Double> counter : server.entrySet()) {
            matchedNameAndValuesForServer.put(getCounterName(counter.getKey(), metaData), counter.getValue());
        }

        return this;
    }

    private String getCounterName(int id, MetaData metaData) {
        for (CounterMeta meta : metaData.getData()) {
            if (meta.getId() == id) {
                return meta.getName();
            }
        }

        return null;
    }

    public Map<String, Double> getClientStatistics() {
        return matchedNameAndValuesForClient;
    }

    public Map<String, Double> getServerStatistics() {
        return matchedNameAndValuesForServer;
    }
}
