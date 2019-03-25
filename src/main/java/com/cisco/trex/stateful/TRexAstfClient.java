package com.cisco.trex.stateful;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.cisco.trex.stateless.model.ApiVersionHandler;
import org.apache.commons.lang.StringUtils;

import com.cisco.trex.ClientBase;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.model.PortStatus;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * TRex Astf Client class
 */
public class TRexAstfClient extends ClientBase {

    private static final Integer API_VERSION_MAJOR = 1;
    private static final Integer API_VERSION_MINOR = 5;
    private static final String ASTF = "ASTF";

    /**
     * @param host
     * @param port
     * @param userName
     */
    public TRexAstfClient(String host, String port, String userName) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.supportedCmds.add("api_sync_v2");
        this.supportedCmds.add("get_supported_cmds");
    }

    @Override
    protected void serverAPISync() throws TRexConnectionException {
        LOGGER.info("Sync API with the TRex");
        Map<String, Object> apiVers = new HashMap<>();
        apiVers.put("major", API_VERSION_MAJOR);
        apiVers.put("minor", API_VERSION_MINOR);
        apiVers.put("name", ASTF);
        TRexClientResult<ApiVersionHandler> result = callMethod("api_sync_v2", apiVers, ApiVersionHandler.class);
        if (result.get() == null) {
            TRexConnectionException e = new TRexConnectionException(
                    "Unable to connect to TRex server. Required API version is " + API_VERSION_MAJOR + "."
                            + API_VERSION_MINOR);
            LOGGER.error("Unable to sync client with TRex server due to: API_H is null.", e.getMessage());
            throw e;
        }
        this.apiH = result.get().getApiH();
        LOGGER.info("Received api_H: {}", apiH);
    }

    private Map<String, Object> createPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_h", apiH);
        if (!StringUtils.isEmpty(masterHandler)) {
            payload.put("handler", masterHandler);
        }
        return payload;
    }

    /**
     * start traffic on all ports on the last loaded profile
     *
     * @param clientMask
     * @param duration
     * @param ipv6
     * @param latencyPps
     * @param mult
     * @param nc
     */
    public void startTraffic(long clientMask, double duration, boolean ipv6,
            int latencyPps, int mult, boolean nc) {
        Map<String, Object> payload = createPayload();
        payload.put("client_mask", clientMask);
        payload.put("duration", duration);
        payload.put("ipv6", ipv6);
        payload.put("latency_pps", latencyPps);
        payload.put("mult", mult);
        payload.put("nc", nc);
        this.callMethod("start", payload);
    }

    /**
     * start latency ICMP streams in Rx core
     *
     * @param mask
     * @param mult
     * @param srcAddr
     * @param dstAddr
     * @param dualPortAddr
     */
    public void startLatencyTraffic(long mask, int mult, String srcAddr, String dstAddr, String dualPortAddr) {
        Map<String, Object> payload = createPayload();
        payload.put("mask", mask);
        payload.put("mult", mult);
        payload.put("src_addr", srcAddr);
        payload.put("dst_addr", dstAddr);
        payload.put("dual_port_addr", dualPortAddr);
        this.callMethod("start_latency", payload);
    }

    /**
     * Stop the active traffic
     */
    public void stopTraffic() {
        Map<String, Object> payload = createPayload();
        this.callMethod("stop", payload);
    }

    /**
     * Stop active latency traffic
     */
    public void stopLatencyTraffic() {
        Map<String, Object> payload = createPayload();
        this.callMethod("stop_latency", payload);
    }

    /**
     * Update the multiplier of running traffic
     *
     * @param mult
     */
    public void updateTrafficRate(int mult) {
        Map<String, Object> payload = createPayload();
        payload.put("mult", mult);
        this.callMethod("update", payload);
    }

    /**
     * Update the multiplier of running traffic
     *
     * @param mult
     */
    public void updateLatencyTrafficRate(int mult) {
        Map<String, Object> payload = createPayload();
        payload.put("mult", mult);
        this.callMethod("update_latency", payload);
    }

    /**
     * Acquire Port
     *
     * @param force
     * @param portIndex
     */
    @Override
    public PortStatus acquirePort(int portIndex, Boolean force) {
        Map<String, Object> payload = createPayload();
        payload.put("user", userName);
        payload.put("force", force);
        payload.put("port_id", portIndex);
        String json = callMethod("acquire", payload);
        JsonElement response = new JsonParser().parse(json);
        Set<Entry<String, JsonElement>> entrySet;
        try {
            this.masterHandler = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonObject()
                    .get("handler").getAsString();
            entrySet = response.getAsJsonArray().get(0).getAsJsonObject().get("result")
                    .getAsJsonObject()
                    .get("ports").getAsJsonObject().entrySet();
        } catch (NullPointerException e) {
            throw new IllegalStateException("could not parse attribute, please release the port first", e);
        }
        if (force) {
            portHandlers.clear();
        }
        for (Entry<String, JsonElement> entry : entrySet) {
            portHandlers.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsString());
        }
        LOGGER.info("portHandlers is: {} ", portHandlers);
        return getPortStatus(portIndex).get();
    }

    /**
     * @param fragFirst
     * @param fragLast
     * @param fragmentData
     * @param totalSize
     */
    public void loadProfile(boolean fragFirst, boolean fragLast, String fragmentData, long totalSize) {
        Map<String, Object> payload = createPayload();
        if (fragFirst) {
            payload.put("frag_first", true);
            payload.put("total_size", totalSize);
        }
        if (fragLast) {
            payload.put("frag_last", true);
        }
        payload.put("fragment", fragmentData);
        this.callMethod("profile_fragment", payload);
    }

    /**
     * clearProfile
     */
    public void clearProfile() {
        Map<String, Object> payload = createPayload();
        this.callMethod("profile_clear", payload);
    }
    
    /**
     * Get Counter Metadata
     * Not finished, needs to return counter object
     */
    public void getCounterMetadata() {
        Map<String, Object> payload = createPayload();
        this.callMethod("get_counter_desc", payload);
    }

    /**
     * Get Astf Counters
     * Not finished, needs to return counter object
     */
    public void getAstfCounters() {
        Map<String, Object> payload = this.createPayload();
        this.callMethod("get_counter_values", payload);
    }

    /**
     * Get Latency Stats
     * Not finished, needs to return counter object
     */
    public void getLatencyStats() {
        Map<String, Object> payload = this.createPayload();
        this.callMethod("get_latency_stats", payload);
    }

    /**
     * Get Version
     * 
     * @return version
     */
    public String getVersion() {
        Map<String, Object> payload = this.createPayload();
        String json = callMethod("get_version", payload);
        JsonElement response = new JsonParser().parse(json);
        try {
            return response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonObject()
                    .get("version").getAsString();
        } catch (NullPointerException e) {
            throw new IllegalStateException("could not parse version", e);
        }
    }

}
