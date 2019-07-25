package com.cisco.trex;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.cisco.trex.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.trex.stateless.TRexCommand;
import com.cisco.trex.stateless.TRexTransport;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.model.L2Configuration;
import com.cisco.trex.stateless.model.Port;
import com.cisco.trex.stateless.model.PortStatus;
import com.cisco.trex.stateless.model.RPCResponse;
import com.cisco.trex.stateless.model.StubResult;
import com.cisco.trex.stateless.model.SystemInfo;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.stateless.model.capture.CaptureInfo;
import com.cisco.trex.stateless.model.capture.CaptureMonitor;
import com.cisco.trex.stateless.model.capture.CaptureMonitorStop;
import com.cisco.trex.stateless.model.capture.CapturedPackets;
import com.cisco.trex.stateless.model.stats.ExtendedPortStatistics;
import com.cisco.trex.stateless.model.stats.PortStatistics;
import com.cisco.trex.stateless.model.stats.XstatsNames;
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
        parameters.put("jsonrpc", Constants.JSON_RPC_VERSION);
        parameters.put("method", methodName);
        payload.put("api_h", apiH);
        parameters.put("params", payload);
        return GSON.toJson(parameters);
    }

    private String call(String json) {
        return this.transport.sendJson(json);
    }

    private List<TRexCommand> buildRemoveCaptureCommand(List<Integer> capture_ids) {
        return capture_ids.stream()
                .map(captureId -> {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("command", "remove");
                    parameters.put("capture_id", captureId);
                    return buildCommand("capture", parameters);
                })
                .collect(Collectors.toList());
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
     * Get Ports
     *
     * @return port list
     */
    public List<Port> getPorts() {
        LOGGER.info("Getting ports list.");
        List<Port> ports = getSystemInfo().getPorts();
        ports.stream().forEach(port -> {
            TRexClientResult<PortStatus> result = getPortStatus(port.getIndex());
            if (result.isFailed()) {
                return;
            }
            PortStatus status = result.get();
            L2Configuration l2config = status.getAttr().getLayerConiguration().getL2Configuration();
            port.hw_mac = l2config.getSrc();
            port.dst_macaddr = l2config.getDst();
        });

        return ports;
    }

    /**
     * @param portIndex
     * @return Port
     */
    public Port getPortByIndex(int portIndex) {
        List<Port> ports = getPorts();
        if (ports.stream().noneMatch(p -> p.getIndex() == portIndex)) {
            LOGGER.error(String.format("Port with index %s was not found. Returning empty port", portIndex));
        }
        return getPorts().stream()
                .filter(p -> p.getIndex() == portIndex)
                .findFirst()
                .orElse(new Port());
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
            RPCResponse response = transport.sendCommand(buildCommand(methodName, parameters));
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
        payload.put("jsonrpc", Constants.JSON_RPC_VERSION);
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
     * Get Port Status including profile transmitting state
     *
     * @param portIdx
     * @param profileId
     * @return PortStatus
     */
    public TRexClientResult<PortStatus> getPortStatus(int portIdx, String profileId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("port_id", portIdx);
        parameters.put("profile_id", profileId);
        parameters.put("block", false);
        return callMethod("get_port_status", parameters, PortStatus.class);
    }

    /**
     * @param portIndex
     * @return PortStatistics
     */
    public PortStatistics getPortStatistics(int portIndex) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("port_id", portIndex);
        return callMethod("get_port_stats", parameters, PortStatistics.class).get();
    }

    /**
     * @param portIndex
     * @return ExtendedPortStatistics
     */
    public ExtendedPortStatistics getExtendedPortStatistics(int portIndex) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("port_id", portIndex);
        return callMethod("get_port_xstats_values", parameters, ExtendedPortStatistics.class).get()
                .setValueNames(getPortStatNames(portIndex));
    }

    private XstatsNames getPortStatNames(int portIndex) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("port_id", portIndex);
        return callMethod("get_port_xstats_names", parameters, XstatsNames.class).get();
    }

    /**
     * Get Active Captures
     *
     * @return CaptureInfo
     */
    public TRexClientResult<CaptureInfo[]> getActiveCaptures() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("command", "status");
        return callMethod("capture", payload, CaptureInfo[].class);
    }

    /**
     * Start Capture Monitor
     *
     * @param rxPorts
     * @param txPorts
     * @param filter
     * @return CaptureMonitor
     */
    public TRexClientResult<CaptureMonitor> captureMonitorStart(
            List<Integer> rxPorts,
            List<Integer> txPorts,
            String filter) {
        return startCapture(rxPorts, txPorts, "cyclic", 100, filter);
    }

    /**
     * Start Capture Recorder
     *
     * @param rxPorts
     * @param txPorts
     * @param filter
     * @param limit
     * @return CaptureMonitor
     */
    public TRexClientResult<CaptureMonitor> captureRecorderStart(
            List<Integer> rxPorts,
            List<Integer> txPorts,
            String filter,
            int limit) {
        return startCapture(rxPorts, txPorts, "fixed", limit, filter);
    }

    /**
     * Start Capture
     *
     * @param rxPorts
     * @param txPorts
     * @param mode
     * @param limit
     * @param filter
     * @return CaptureMonitor
     */
    public TRexClientResult<CaptureMonitor> startCapture(
            List<Integer> rxPorts,
            List<Integer> txPorts,
            String mode,
            int limit,
            String filter) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("command", "start");
        payload.put("limit", limit);
        payload.put("mode", mode);
        payload.put("rx", rxPorts);
        payload.put("tx", txPorts);
        payload.put("filter", filter);
        return callMethod("capture", payload, CaptureMonitor.class);
    }

    /**
     * Remove All Captures
     *
     * @return response
     */
    public TRexClientResult<List<RPCResponse>> removeAllCaptures() {
        TRexClientResult<CaptureInfo[]> activeCaptures = getActiveCaptures();
        List<Integer> captureIds = Arrays.stream(activeCaptures.get()).map(CaptureInfo::getId)
                .collect(Collectors.toList());
        List<TRexCommand> commands = buildRemoveCaptureCommand(captureIds);
        return callMethods(commands);
    }

    /**
     * Stop Capture Monitor
     *
     * @param captureId
     * @return CaptureMonitorStop
     */
    public TRexClientResult<CaptureMonitorStop> captureMonitorStop(int captureId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("command", "stop");
        payload.put("capture_id", captureId);
        return callMethod("capture", payload, CaptureMonitorStop.class);
    }

    /**
     * Remove Capture Monitor
     *
     * @param captureId
     * @return successfully removed
     */
    public boolean captureMonitorRemove(int captureId) {
        List<TRexCommand> commands = buildRemoveCaptureCommand(Collections.singletonList(captureId));
        TRexClientResult<List<RPCResponse>> result = callMethods(commands);
        if (result.isFailed()) {
            LOGGER.error("Unable to remove recorder due to: {}", result.getError());
            return false;
        }
        Optional<RPCResponse> failed = result.get().stream().filter(RPCResponse::isFailed).findFirst();
        return !failed.isPresent();
    }

    /**
     * Fetch Captured Packets
     *
     * @param captureId
     * @param chunkSize
     * @return CapturedPackets
     */
    public TRexClientResult<CapturedPackets> captureFetchPkts(int captureId, int chunkSize) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("command", "fetch");
        payload.put("capture_id", captureId);
        payload.put("pkt_limit", chunkSize);
        return callMethod("capture", payload, CapturedPackets.class);
    }

    /**
     * Set Vlan
     *
     * @param portIdx
     * @param vlanIds
     * @return StubResult
     */
    public TRexClientResult<StubResult> setVlan(int portIdx, List<Integer> vlanIds) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("port_id", portIdx);
        parameters.put("vlan", vlanIds);
        parameters.put("block", false);
        return callMethod("set_vlan", parameters, StubResult.class);
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
    
    /**
     * Release Port
     *
     * @param portIndex
     * @return PortStatus
     */
    public PortStatus releasePort(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("user", userName);
        String result = callMethod("release", payload);
        if (result.contains("must acquire the context")) {
            LOGGER.info("Port is not owned by this session, already released or never acquired");
        }
        portHandlers.remove(portIndex);
        return getPortStatus(portIndex).get();
    }

    protected Map<String, Object> createPayload(int portIndex) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("port_id", portIndex);
        payload.put("api_h", apiH);
        if (!StringUtils.isEmpty(masterHandler)) {
            payload.put("handler", masterHandler);
        } else {
            String handler = portHandlers.get(portIndex);
            if (handler != null) {
                payload.put("handler", handler);
            }
        }
        return payload;
    }
    
    protected Map<String, Object> createPayload(int portIndex, String profileId) {
        Map<String, Object> payload = createPayload(portIndex);
        if (profileId != null && !profileId.isEmpty()) {
            payload.put("profile_id", profileId);
        }
        return payload;
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

}
