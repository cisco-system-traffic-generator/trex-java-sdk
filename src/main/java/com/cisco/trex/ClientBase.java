package com.cisco.trex;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.cisco.trex.stateless.model.PortStatus;
import com.cisco.trex.stateless.model.RPCResponse;
import com.cisco.trex.stateless.model.SystemInfo;
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
 * Base class for stateful and stateless classes
 */
public abstract class ClientBase {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ClientBase.class);
    protected static final String JSON_RPC_VERSION = "2.0";
    protected static final Gson GSON = buildGson();
    protected String host;
    protected String port;
    protected String userName = "";
    protected final Map<Integer, String> portHandlers = new HashMap<>();
    protected final Set<String> supportedCmds = new HashSet<>();
    protected final Random randomizer = new Random();
    protected TRexTransport transport;
    protected String apiH;
    protected String masterHandler;

    private static Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>() {}.getType(),
                new DoubleAsIntDeserializer());
        return gsonBuilder.create();
    }

    private String buildRequest(String methodName, Map<String, Object> payload) {
        if (payload == null) {
            payload = new HashMap<>();
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", "aggogxls");
        parameters.put("jsonrpc", JSON_RPC_VERSION);
        parameters.put("method", methodName);
        payload.put("api_h", apiH);
        parameters.put("params", payload);
        return GSON.toJson(parameters);
    }

    private String call(String json) {
        return this.transport.sendJson(json);
    }

    private class SystemInfoResponse {

        private String id;
        private String jsonrpc;
        private SystemInfo result;

        public SystemInfo getResult() {
            return result;
        }
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
        return call(req);
    }

    /**
     * call Methods
     *
     * @param commands
     * @return results
     */
    public TRexClientResult<List<RPCResponse>> callMethods(List<TRexCommand> commands) {
        TRexClientResult<List<RPCResponse>> result = new TRexClientResult<>();

        try {
            RPCResponse[] rpcResponses = transport.sendCommands(commands);
            result.set(Arrays.asList(rpcResponses));
        } catch (IOException e) {
            String msg = "Unable to send RPC Batch";
            result.setError(msg);
            LOGGER.error(msg, e);
        }
        return result;
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
            RPCResponse response = transport.sendCommand(this.buildCommand(methodName, parameters));
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

    /**
     * Build Command
     *
     * @param methodName
     * @param parameters
     * @return TRexCommand
     */
    public TRexCommand buildCommand(String methodName, Map<String, Object> parameters) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put("api_h", apiH);
        Map<String, Object> payload = new HashMap<>();
        int cmdId = randomizer.nextInt() & Integer.MAX_VALUE; //get a positive random value
        payload.put("id", cmdId);
        payload.put("jsonrpc", JSON_RPC_VERSION);
        payload.put("method", methodName);
        if (!StringUtils.isEmpty(this.masterHandler)) {
            payload.put("handler", this.masterHandler);
        }

        payload.put("params", parameters);
        return new TRexCommand(cmdId, methodName, payload);
    }

    /**
     * Disconnect from trex
     */
    public void disconnect() {
        if (transport != null) {
            transport.close();
            transport = null;
            LOGGER.info("Disconnected");
        } else {
            LOGGER.info("Already disconnected");
        }
    }

    /**
     * Reconnect
     *
     * @throws TRexConnectionException
     */
    public void reconnect() throws TRexConnectionException {
        disconnect();
        connect();
    }

    /**
     * connect with default timeout 3000 mSec
     *
     * @throws TRexConnectionException
     */
    public final void connect() throws TRexConnectionException {
        connect(3000);
    }

    /**
     * connect with timeout
     *
     * @param timeout
     * @throws TRexConnectionException
     */
    public void connect(int timeout) throws TRexConnectionException {
        transport = new TRexTransport(this.host, this.port, timeout);
        serverAPISync();
        supportedCmds.addAll(getSupportedCommands());
    }

    /**
     * Get Supported Commands
     *
     * @return Supported Commands
     */
    public List<String> getSupportedCommands() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_h", apiH);
        String json = callMethod("get_supported_cmds", payload);
        JsonElement response = new JsonParser().parse(json);
        JsonArray cmds = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonArray();
        return StreamSupport.stream(cmds.spliterator(), false)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
    }

    /**
     * Get Port Status
     *
     * @param portIdx
     * @return PortStatus
     */
    public TRexClientResult<PortStatus> getPortStatus(int portIdx) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("port_id", portIdx);
        parameters.put("block", false);
        return callMethod("get_port_status", parameters, PortStatus.class);
    }

    /**
     * Get System Information
     * 
     * @return SystemInfo
     */
    public SystemInfo getSystemInfo() {
        String json = callMethod("get_system_info", null);
        SystemInfoResponse response = GSON.fromJson(json, SystemInfoResponse[].class)[0];
        return response.getResult();
    }

    protected abstract void serverAPISync() throws TRexConnectionException;

    /**
     * Acquire Port to be able to apply configuration to it
     *
     * @param port
     * @param force
     * @return PortStatus
     */
    public abstract PortStatus acquirePort(int port, Boolean force);

    /**
     * Release Port
     *
     * @param portIndex
     * @return PortStatus
     */
    public abstract PortStatus releasePort(int portIndex);

}
