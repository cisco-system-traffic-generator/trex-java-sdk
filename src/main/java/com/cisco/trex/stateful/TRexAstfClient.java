package com.cisco.trex.stateful;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.trex.stateless.TRexCommand;
import com.cisco.trex.stateless.TRexTransport;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.model.RPCResponse;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.stateless.util.DoubleAsIntDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * TRex Astf Client class
 */
public class TRexAstfClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TRexAstfClient.class);
    private static final String JSON_RPC_VERSION = "2.0";
    private static final Integer API_VERSION_MAJOR = 1;
    private static final Integer API_VERSION_MINOR = 1;
    private static final String ASTF = "ASTF";
    private TRexTransport transport;
    private static final Gson GSON = TRexAstfClient.buildGson();
    private final String host;
    private final String port;
    private final String userName;
    private String apiH;
    private String masterHandler;
    private final Map<Integer, String> portHandlers = new HashMap<>();
    private final Set<String> supportedCmds = new HashSet<>();
    private final Random randomizer = new Random();

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

    private static Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>() {}.getType(),
                new DoubleAsIntDeserializer());
        return gsonBuilder.create();
    }

    private String buildRequest(String methodName, Map<String, Object> payload) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", "aggogxls");
        parameters.put("jsonrpc", JSON_RPC_VERSION);
        parameters.put("method", methodName);
        payload.put("api_h", this.apiH);
        parameters.put("params", payload);
        return TRexAstfClient.GSON.toJson(parameters);
    }

    private String call(String json) {
        return this.transport.sendJson(json);
    }

    /**
     * call Method
     *
     * @param methodName
     * @param payload
     * @return result
     */
    public String callMethod(String methodName, Map<String, Object> payload) {
        LOGGER.info("Call {} method.", methodName);
        if (!this.supportedCmds.contains(methodName)) {
            LOGGER.error("Unsupported {} method.", methodName);
            throw new UnsupportedOperationException();
        }
        String req = this.buildRequest(methodName, payload);
        return this.call(req);
    }

    /**
     * call method
     *
     * @param <T>
     * @param methodName
     * @param parameters
     * @param responseType
     * @return result
     */
    public <T> TRexClientResult<T> callMethod(String methodName, Map<String, Object> parameters,
            Class<T> responseType) {
        LOGGER.info("Call {} method.", methodName);
        if (!this.supportedCmds.contains(methodName)) {
            LOGGER.error("Unsupported {} method.", methodName);
            throw new UnsupportedOperationException();
        }
        TRexClientResult<T> result = new TRexClientResult<>();
        try {
            RPCResponse response = this.transport.sendCommand(this.buildCommand(methodName, parameters));
            if (!response.isFailed()) {
                T resutlObject = new ObjectMapper().readValue(response.getResult(), responseType);
                result.set(resutlObject);
            } else {
                result.setError(response.getError().getMessage() + " " + response.getError().getSpecificErr());
            }

            return result;
        } catch (IOException var7) {
            String errorMsg = "Error occurred during processing '" + methodName + "' method with params: "
                    + parameters.toString();
            LOGGER.error(errorMsg, var7);
            result.setError(errorMsg);
            return result;
        }
    }

    private TRexCommand buildCommand(String methodName, Map<String, Object> parameters) {
        parameters.putAll(parameters);
        parameters.put("api_h", this.apiH);
        Map<String, Object> payload = new HashMap<>();
        int cmdId = this.randomizer.nextInt() & Integer.MAX_VALUE; //get a positive random value
        payload.put("id", cmdId);
        payload.put("jsonrpc", JSON_RPC_VERSION);
        payload.put("method", methodName);
        if (!StringUtils.isEmpty(this.masterHandler)) {
            parameters.put("handler", this.masterHandler);
        }

        payload.put("params", parameters);
        return new TRexCommand(cmdId, methodName, payload);
    }

    /**
     * connect with default timeout 3000 mSec
     *
     * @throws TRexConnectionException
     */
    public void connect() throws TRexConnectionException {
        this.connect(3000);
    }

    /**
     * connect with timeout
     *
     * @param timeout
     * @throws TRexConnectionException
     */
    public void connect(int timeout) throws TRexConnectionException {
        this.transport = new TRexTransport(this.host, this.port, timeout);
        this.serverAPISync();
        this.supportedCmds.addAll(this.getSupportedCommands());
    }

    private void serverAPISync() throws TRexConnectionException {
        LOGGER.info("Sync API with the TRex");
        Map<String, Object> apiVers = new HashMap<>();
        apiVers.put("major", API_VERSION_MAJOR);
        apiVers.put("minor", API_VERSION_MINOR);
        apiVers.put("name", ASTF);
        TRexClientResult<ApiAstfVersion> result = this.callMethod("api_sync_v2", apiVers, ApiAstfVersion.class);
        if (result.get() == null) {
            TRexConnectionException e = new TRexConnectionException(
                    "Unable to connect to TRex server. Required API version is " + API_VERSION_MAJOR + "."
                            + API_VERSION_MINOR);
            LOGGER.error("Unable to sync client with TRex server due to: API_H is null.", e.getMessage());
            throw e;
        }
        this.apiH = result.get().getApiH();
        LOGGER.info("Received api_H: {}", this.apiH);
    }

    /**
     * Disconnect from trex
     */
    public void disconnect() {
        if (this.transport != null) {
            this.transport.close();
            this.transport = null;
            LOGGER.info("Disconnected");
        } else {
            LOGGER.info("Already disconnected");
        }

    }

    private Map<String, Object> createPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_h", this.apiH);
        if (!StringUtils.isEmpty(this.masterHandler)) {
            payload.put("handler", this.masterHandler);
        }
        return payload;
    }

    private List<String> getSupportedCommands() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_h", this.apiH);
        String json = this.callMethod("get_supported_cmds", payload);
        JsonElement response = new JsonParser().parse(json);
        JsonArray cmds = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonArray();
        return StreamSupport.stream(cmds.spliterator(), false).map(JsonElement::getAsString)
                .collect(Collectors.toList());
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
        Map<String, Object> payload = this.createPayload();
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
        Map<String, Object> payload = this.createPayload();
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
        Map<String, Object> payload = this.createPayload();
        this.callMethod("stop", payload);
    }

    /**
     * Stop active latency streams
     */
    public void stopLatencyTraffic() {
        Map<String, Object> payload = this.createPayload();
        this.callMethod("stop_latency", payload);
    }

    /**
     * Update the multiplier of running traffic
     *
     * @param mult
     */
    public void updateTrafficRate(int mult) {
        Map<String, Object> payload = this.createPayload();
        payload.put("mult", mult);
        this.callMethod("update", payload);
    }

    /**
     * Update the multiplier of running traffic
     *
     * @param mult
     */
    public void updateLatencyTrafficRate(int mult) {
        Map<String, Object> payload = this.createPayload();
        payload.put("mult", mult);
        this.callMethod("update_latency", payload);
    }

    /**
     * Acquire Port
     *
     * @param force
     * @param port
     */
    public void acquirePort(Boolean force, int port) {
        Map<String, Object> payload = this.createPayload();
        payload.put("user", this.userName);
        payload.put("force", force);
        payload.put("port_id", port);
        String json = this.callMethod("acquire", payload);
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
            this.portHandlers.clear();
        }
        for (Entry<String, JsonElement> entry : entrySet) {
            this.portHandlers.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsString());
        }
        LOGGER.info("portHandlers is: {} ", portHandlers);
    }

    /**
     * Release Port
     *
     * @param port
     */
    public void releasePort(int port) {
        Map<String, Object> payload = this.createPayload();
        payload.put("port_id", port);
        this.callMethod("release", payload);
    }

    /**
     * @param fragFirst
     * @param fragLast
     * @param fragmentData
     * @param totalSize
     */
    public void loadProfile(boolean fragFirst, boolean fragLast, String fragmentData, long totalSize) {
        Map<String, Object> payload = this.createPayload();
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
        Map<String, Object> payload = this.createPayload();
        this.callMethod("profile_clear", payload);
    }

}
