package com.cisco.trex;

import com.cisco.trex.model.GlobalConfig;
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
import com.cisco.trex.stateless.model.stats.GlobalStatistics;
import com.cisco.trex.stateless.model.stats.PortStatistics;
import com.cisco.trex.stateless.model.stats.XstatsNames;
import com.cisco.trex.stateless.util.DoubleAsIntDeserializer;
import com.cisco.trex.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base class for stateful and stateless classes */
public abstract class ClientBase {

  private static final String CAPTURE_ID = "capture_id";
  private static final String CAPTURE = "capture";
  private static final String COMMAND = "command";
  protected static final String API_H = "api_h";
  private static final String RESULT = "result";
  protected static final String PORT_ID = "port_id";
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
  private XstatsNames xstatsNames;

  private static Gson buildGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(
        new TypeToken<Map<String, Object>>() {}.getType(), new DoubleAsIntDeserializer());
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
    payload.put(API_H, apiH);
    parameters.put("params", payload);
    return GSON.toJson(parameters);
  }

  private String call(String json) {
    return this.transport.sendJson(json);
  }

  private List<TRexCommand> buildRemoveCaptureCommand(List<Integer> captureIds) {
    return captureIds.stream()
        .map(
            captureId -> {
              Map<String, Object> parameters = new HashMap<>();
              parameters.put(COMMAND, "remove");
              parameters.put(CAPTURE_ID, captureId);
              return buildCommand(CAPTURE, parameters);
            })
        .collect(Collectors.toList());
  }

  /**
   * Get Ports
   *
   * @return port list
   */
  public List<Port> getPorts() {
    LOGGER.debug("Getting ports list.");
    List<Port> ports = getSystemInfo().getPorts();
    ports.stream()
        .forEach(
            port -> {
              TRexClientResult<PortStatus> result = getPortStatus(port.getIndex());
              if (result.isFailed()) {
                return;
              }
              PortStatus status = result.get();
              L2Configuration l2config =
                  status.getAttr().getLayerConiguration().getL2Configuration();
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
      LOGGER.error(
          String.format("Port with index %s was not found. Returning empty port", portIndex));
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
    LOGGER.debug("Call {} method.", methodName);
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
  public <T> TRexClientResult<T> callMethod(
      String methodName, Map<String, Object> parameters, Class<T> responseType) {
    LOGGER.debug("Call {} method.", methodName);
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
        result.setError(
            response.getError().getMessage() + " " + response.getError().getSpecificErr());
      }

      return result;
    } catch (IOException var7) {
      String errorMsg =
          "Error occurred during processing '"
              + methodName
              + "' method with params: "
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
    parameters.put(API_H, apiH);
    Map<String, Object> payload = new HashMap<>();
    int cmdId = randomizer.nextInt() & Integer.MAX_VALUE; // get a positive random value
    payload.put("id", cmdId);
    payload.put("jsonrpc", Constants.JSON_RPC_VERSION);
    payload.put("method", methodName);
    if (!StringUtils.isEmpty(this.masterHandler)) {
      payload.put("handler", this.masterHandler);
    }
    payload.put("params", parameters);
    return new TRexCommand(cmdId, methodName, payload);
  }

  /** Disconnect from trex */
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
    payload.put(API_H, apiH);
    String json = callMethod("get_supported_cmds", payload);
    JsonArray cmds = getResultFromResponse(json).getAsJsonArray();
    return StreamSupport.stream(cmds.spliterator(), false)
        .map(JsonElement::getAsString)
        .collect(Collectors.toList());
  }

  protected JsonElement getResultFromResponse(String json) {
    JsonElement response = new JsonParser().parse(json);
    if (response.isJsonArray()) {
      // for versions of TRex before v2.61, single entry response also wrapped with
      // json array
      return response.getAsJsonArray().get(0).getAsJsonObject().get(RESULT);
    }

    return response.getAsJsonObject().get(RESULT);
  }

  /**
   * Get Port Status
   *
   * @param portIdx
   * @return PortStatus
   */
  public TRexClientResult<PortStatus> getPortStatus(int portIdx) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(PORT_ID, portIdx);
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
    parameters.put(PORT_ID, portIdx);
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
    parameters.put(PORT_ID, portIndex);
    return callMethod("get_port_stats", parameters, PortStatistics.class).get();
  }

  /** @return GlobalStatistics */
  public GlobalStatistics getGlobalStatistics() {
    return callMethod("get_global_stats", null, GlobalStatistics.class).get();
  }

  /**
   * @param portIndex
   * @return ExtendedPortStatistics
   */
  public ExtendedPortStatistics getExtendedPortStatistics(int portIndex) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(PORT_ID, portIndex);
    return callMethod("get_port_xstats_values", parameters, ExtendedPortStatistics.class)
        .get()
        .setValueNames(getPortStatNames(portIndex));
  }

  private XstatsNames getPortStatNames(int portIndex) {
    if (xstatsNames == null) {
      Map<String, Object> parameters = new HashMap<>();
      parameters.put(PORT_ID, portIndex);
      xstatsNames = callMethod("get_port_xstats_names", parameters, XstatsNames.class).get();
    }

    return xstatsNames;
  }

  /**
   * Get Active Captures
   *
   * @return CaptureInfo
   */
  public TRexClientResult<CaptureInfo[]> getActiveCaptures() {
    Map<String, Object> payload = new HashMap<>();
    payload.put(COMMAND, "status");
    return callMethod(CAPTURE, payload, CaptureInfo[].class);
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
      List<Integer> rxPorts, List<Integer> txPorts, String filter) {
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
      List<Integer> rxPorts, List<Integer> txPorts, String filter, int limit) {
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
      List<Integer> rxPorts, List<Integer> txPorts, String mode, int limit, String filter) {
    return startCapture(rxPorts, txPorts, mode, limit, filter, "", 0);
  }

  /**
   * Start Capture
   *
   * @param rxPorts
   * @param txPorts
   * @param mode
   * @param limit
   * @param filter
   * @param endpoint
   * @param snaplen
   * @return CaptureMonitor
   */
  public TRexClientResult<CaptureMonitor> startCapture(
      List<Integer> rxPorts,
      List<Integer> txPorts,
      String mode,
      int limit,
      String filter,
      String endpoint,
      int snaplen) {
    Map<String, Object> payload = new HashMap<>();
    payload.put(COMMAND, "start");
    payload.put("limit", limit);
    payload.put("mode", mode);
    payload.put("rx", rxPorts);
    payload.put("tx", txPorts);
    payload.put("filter", filter);
    if (endpoint != null && !endpoint.isEmpty()) {
      payload.put("endpoint", endpoint);
    }
    if (snaplen != 0) {
      payload.put("snaplen", snaplen);
    }
    return callMethod(CAPTURE, payload, CaptureMonitor.class);
  }

  /**
   * Remove All Captures
   *
   * @return response
   */
  public TRexClientResult<List<RPCResponse>> removeAllCaptures() {
    TRexClientResult<CaptureInfo[]> activeCaptures = getActiveCaptures();
    List<Integer> captureIds =
        Arrays.stream(activeCaptures.get()).map(CaptureInfo::getId).collect(Collectors.toList());
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
    payload.put(COMMAND, "stop");
    payload.put(CAPTURE_ID, captureId);
    return callMethod(CAPTURE, payload, CaptureMonitorStop.class);
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
   * @param packetLimit amount of packets to capture
   * @return CapturedPackets
   */
  public TRexClientResult<CapturedPackets> captureFetchPkts(int captureId, int packetLimit) {
    Map<String, Object> payload = new HashMap<>();
    payload.put(COMMAND, "fetch");
    payload.put(CAPTURE_ID, captureId);
    payload.put("pkt_limit", packetLimit);
    return callMethod(CAPTURE, payload, CapturedPackets.class);
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
    parameters.put(PORT_ID, portIdx);
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
    return GSON.fromJson(getResultFromResponse(json), SystemInfo.class);
  }

  /**
   * Get global configuration parameters
   *
   * @return GlobalConfig
   */
  public TRexClientResult<GlobalConfig> getGlobalConfig(int portIdx) {
    Map<String, Object> payload = new HashMap<>();
    return callMethod("get_global_cfg", null, GlobalConfig.class);
  }

  /**
   * Change global configuration parameter
   *
   * @param name parameter name
   * @param value parameter value in data types of double, boolean depending on the parameter type
   * @return StubResult
   */
  public TRexClientResult<StubResult> setGlobalConfig(String name, Object value) {
    Map<String, Object> payload = new HashMap<>();
    payload.put(name, value);
    return callMethod("set_global_cfg", payload, StubResult.class);
  }

  /**
   * Change single or multiple global configuration parameter(s)
   *
   * @param parameters a map contains parameter name and value pairs, parameter value is in data types of double,
   *                   boolean depending on the parameter type.
   * @return StubResult
   */
  public TRexClientResult<StubResult> setGlobalConfig(Map<String, Object> parameters) {
    Map<String, Object> payload = new HashMap<>();
    parameters.forEach((key, value) -> payload.put(key, value));
    return callMethod("set_global_cfg", payload, StubResult.class);
  }

  protected Map<String, Object> createPayload(int portIndex) {
    Map<String, Object> payload = new HashMap<>();
    payload.put(PORT_ID, portIndex);
    payload.put(API_H, apiH);
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
}
