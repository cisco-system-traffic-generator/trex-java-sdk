package com.cisco.trex.stateless;

import com.cisco.trex.stateless.model.*;
import com.google.gson.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TRexClient {

    private static final Logger logger = LoggerFactory.getLogger(TRexClient.class);
    
    private static String JSON_RPC_VERSION = "2.0";

    private static Integer API_VERSION_MAJOR = 3;

    private static Integer API_VERSION_MINOR = 0;
    
    private ZMQ.Context zmqCtx = ZMQ.context(1);

    private ZMQ.Socket zmqSocket;
    
    private Gson gson = new Gson();

    private String protocol;
    
    private String host;
    
    private String port;
    
    private String asyncPort;
    
    private String api_h;
    
    private String userName = "";
    private Integer session_id = 123456789;
    private Map<Integer, String> portHandlers = new HashMap<>();
    private List<String> supportedCmds = new ArrayList<>();

    public TRexClient(String protocol, String host, String port, String userName) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.userName = userName;
        supportedCmds.add("api_sync");
        supportedCmds.add("get_supported_cmds");
    }
    
    public String callMethod(String methodName, Map<String, Object> payload) {
        logger.info("Call {} method.", methodName);
        if (!supportedCmds.contains(methodName)) {
            logger.error("Unsupported {} method.", methodName);
            throw new UnsupportedOperationException();
        }
        String req = buildRequest(methodName, payload);
        return call(req);
    }

    private String buildRequest(String methodName, Map<String, Object> payload) {
        if (payload == null) {
            payload = new LinkedHashMap<>();
        }
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("id", "aggogxls");
        parameters.put("jsonrpc", JSON_RPC_VERSION);
        parameters.put("method", methodName);
        
        payload.put("api_h", api_h);
        
        parameters.put("params", payload);
        return gson.toJson(parameters);
    }

    private String call(String json) {
        logger.info("JSON Req: " + json);
        zmqSocket.send(json);
        String response = new String(zmqSocket.recv(0));
        logger.info("JSON Resp: " + response);
        return response;
    }
    
    public void connect() {
        String connectionUrl = getConnectionAddress();
        logger.info("Connect to {}", connectionUrl);
        zmqSocket = zmqCtx.socket(ZMQ.REQ);
        zmqSocket.connect(connectionUrl);
        serverAPISync();
        supportedCmds.addAll(getSupportedCommands());
    }

    private String getConnectionAddress() {
        return "tcp://"+host+":"+port;
    }

    private void serverAPISync() {
        logger.info("Sync API with the TRex");
        Map<String, Object> parameters = new LinkedHashMap<>();
        Map<String, Object> api_vers = new LinkedHashMap<>();
        api_vers.put("type", "core");
        api_vers.put("major", API_VERSION_MAJOR);
        api_vers.put("minor", API_VERSION_MINOR);
        parameters.put("api_vers", Arrays.asList(api_vers));
        String responseString = callMethod("api_sync", parameters);
        responseString = responseString.trim();
        ApiVersionResponse response = gson.fromJson(responseString, ApiVersionResponse[].class)[0];
        api_h = response.getApi_h();
        logger.info("Received api_H: {}", api_h);
    }

    public void disconnect() {
        zmqSocket.disconnect(getConnectionAddress());
        logger.info("Disconnected");
    }

    public void reconnect() {
        disconnect();
        connect();
    }

    public List<Port> getPorts() {
        logger.info("Getting ports list.");
        return getSystemInfo().getPorts();
    }

    public PortStatus getPortStatus(int portIdx) {
        Map<String, Object> payload = createPayload(portIdx);
        String json = callMethod("get_port_status", payload);
        JsonElement response = new JsonParser().parse(json);
        return PortStatus.fromJson(response.getAsJsonArray().get(0).getAsJsonObject().getAsJsonObject("result"));
    }

    public PortStatus acquirePort(int portIndex, Boolean force) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("session_id", session_id);
        payload.put("user", userName);
        payload.put("force", force);
        String json = callMethod("acquire", payload);
        JsonElement response = new JsonParser().parse(json);
        String handler = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsString();
        portHandlers.put(portIndex, handler);
        return getPortStatus(portIndex);
    }

    private Map<String, Object> createPayload(int portIndex) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("port_id", portIndex);
        payload.put("api_h", api_h);
        String handler = portHandlers.get(portIndex);
        if (handler != null) {
            payload.put("handler", handler);
        }
        return payload;
    }

    public PortStatus releasePort(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        
        payload.put("user", userName);
        String result = callMethod("release", payload);
        portHandlers.remove(portIndex);
        return getPortStatus(portIndex);
    }

    public List<String> getSupportedCommands() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("api_h", api_h);
        String json = callMethod("get_supported_cmds", payload);
        JsonElement response = new JsonParser().parse(json);
        JsonArray cmds = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonArray();
        return StreamSupport.stream(cmds.spliterator(), false)
            .map(JsonElement::getAsString)
            .collect(Collectors.toList());
    }

    public PortStatus serviceMode(int portIndex, Boolean isOn) {
        logger.info("Set service mode : {}", isOn ? "on" : "off");
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("enabled", isOn);
        String result = callMethod("service", payload);
        return getPortStatus(portIndex);
    }
    
    public void addStream(int portIndex, Stream stream) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("stream_id", stream.getId());
        payload.put("stream", stream);
        callMethod("add_stream", payload);
    }
    
    public Stream getStream(int portIndex, int streamId) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("get_pkt", true);
        payload.put("stream_id", streamId);

        String json = callMethod("get_stream", payload);
        JsonElement response = new JsonParser().parse(json);
        JsonObject stream = response.getAsJsonArray().get(0)
                .getAsJsonObject().get("result")
                .getAsJsonObject().get("stream")
                .getAsJsonObject();
        return gson.fromJson(stream, Stream.class);
    }
    
    public void removeStream(int portIndex, int streamId) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("stream_id", streamId);
        callMethod("remove_stream", payload);
    }
    
    public void removeAllStreams(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        callMethod("remove_all_streams", payload);
    }
    
    public List<Integer> getStreamIds(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        String json = callMethod("get_stream_list", payload);
        JsonElement response = new JsonParser().parse(json);
        JsonArray ids = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonArray();
        return StreamSupport.stream(ids.spliterator(), false)
                .map(JsonElement::getAsInt)
                .collect(Collectors.toList());
    }
    
    public SystemInfo getSystemInfo() {
        String json = callMethod("get_system_info", null);
        SystemInfoResponse response = gson.fromJson(json, SystemInfoResponse[].class)[0];
        return response.getResult();
    }

    public void startTraffic(int portIndex, double duration, boolean force, Map<String, Object> mul, int coreMask) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("core_mask", coreMask);
        payload.put("mul", mul);
        payload.put("duration", duration);
        payload.put("force", force);
        callMethod("start_traffic", payload);
    }

    public void setRxQueue(int portIndex, int size) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("type", "queue");
        payload.put("enabled", true);
        payload.put("size", size);
        callMethod("set_rx_feature", payload);
    }

    public void removeRxQueue(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        callMethod("remove_rx_filters", payload);
    }
    
    public void sendPacket(int portIndex, Packet pkt) {
        Stream stream = build1PktSingleBurstStream(pkt);
        
        removeAllStreams(portIndex);
        addStream(portIndex, stream);
        
        Map<String, Object> mul = new HashMap<>();
        mul.put("op", "abs");
        mul.put("type", "pps");
        mul.put("value", 1.0);
        startTraffic(portIndex, 1, true, mul, 1);
        stopTraffic(portIndex);
    }
    
    private Stream build1PktSingleBurstStream(Packet pkt) {
        int stream_id = (int) (Math.random() * 1000);
        return new Stream(
                stream_id,
                true,
                0.0,
                new StreamMode(
                        1,
                        1,
                        1,
                        1.0,
                        new StreamModeRate(
                                StreamModeRate.Type.pps,
                                1.0
                        ),
                        StreamMode.Type.single_burst
                ),
                -1,
                pkt,
                new StreamRxStats(true, true, true, stream_id),
                new StreamVM("", Collections.<VMInstruction>emptyList()),
                true
        );
    }
    
    public List<Packet> getRxQueue(int portIndex, Predicate<EthernetPacket> filter) {

        Map<String, Object> payload = createPayload(portIndex);
        String json = callMethod("get_rx_queue_pkts", payload);
        JsonElement response = new JsonParser().parse(json);
        JsonArray pkts = response.getAsJsonArray().get(0)
                .getAsJsonObject().get("result")
                .getAsJsonObject()
                .getAsJsonArray("pkts");
        return StreamSupport.stream(pkts.spliterator(), false)
                .map(this::buildEthernetPkt)
                .filter(filter)
                .collect(Collectors.toList());
    }
    
    private EthernetPacket buildEthernetPkt(JsonElement jsonElement) {
        try {
            byte[] binary = Base64.getDecoder().decode(jsonElement.getAsJsonObject().get("binary").getAsString());
            EthernetPacket pkt = EthernetPacket.newPacket(binary, 0, binary.length);
            logger.info("Received pkt: {}", pkt.toString());
            return pkt;
        } catch (IllegalRawDataException e) {
            return null;
        }
    }

    public void stopTraffic(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        callMethod("stop_traffic", payload);
    }
    
    private class ApiVersionResponse {
        private String id;
        private String jsonrpc;
        private ApiVersionResult result;

        private class ApiVersionResult {
            private List<Map<String, String>> api_vers;

            public ApiVersionResult(List<Map<String, String>> api_vers) {
                this.api_vers = api_vers;
            }

            public String getApi_h() {
                return api_vers.get(0).get("api_h");
            }
        }

        public ApiVersionResponse(String id, String jsonrpc, ApiVersionResult result) {
            this.id = id;
            this.jsonrpc = jsonrpc;
            this.result = result;
        }
        
        public String getApi_h() {
            return result.getApi_h();
        }
    }

    private class SystemInfoResponse {
        private String id;
        private String jsonrpc;
        private SystemInfo result;
        public SystemInfo getResult() {
            return result;
        }
    }
}
