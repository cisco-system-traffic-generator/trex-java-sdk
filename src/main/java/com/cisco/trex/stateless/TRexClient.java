package com.cisco.trex.stateless;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.Dot1qVlanTagPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV4CommonPacket;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.IcmpV4EchoReplyPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV4Rfc791Tos;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IcmpV4Code;
import org.pcap4j.packet.namednumber.IcmpV4Type;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

import com.cisco.trex.ClientBase;
import com.cisco.trex.stateless.exception.ServiceModeRequiredException;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.model.ApiVersion;
import com.cisco.trex.stateless.model.Ipv6Node;
import com.cisco.trex.stateless.model.L2Configuration;
import com.cisco.trex.stateless.model.Port;
import com.cisco.trex.stateless.model.PortStatus;
import com.cisco.trex.stateless.model.RPCResponse;
import com.cisco.trex.stateless.model.Stream;
import com.cisco.trex.stateless.model.StreamMode;
import com.cisco.trex.stateless.model.StreamModeRate;
import com.cisco.trex.stateless.model.StreamRxStats;
import com.cisco.trex.stateless.model.StreamVM;
import com.cisco.trex.stateless.model.StubResult;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.stateless.model.capture.CaptureInfo;
import com.cisco.trex.stateless.model.capture.CaptureMonitor;
import com.cisco.trex.stateless.model.capture.CaptureMonitorStop;
import com.cisco.trex.stateless.model.capture.CapturedPackets;
import com.cisco.trex.stateless.model.port.PortVlan;
import com.cisco.trex.stateless.model.stats.ActivePGIds;
import com.cisco.trex.stateless.model.stats.ActivePGIdsRPCResult;
import com.cisco.trex.stateless.model.stats.ExtendedPortStatistics;
import com.cisco.trex.stateless.model.stats.PGIdStatsRPCResult;
import com.cisco.trex.stateless.model.stats.PortStatistics;
import com.cisco.trex.stateless.model.stats.XstatsNames;
import com.cisco.trex.stateless.model.vm.VMInstruction;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TRexClient extends ClientBase {

    private static final EtherType QInQ = new EtherType((short) 0x88a8, "802.1Q Provider Bridge (Q-in-Q)");
    private static Integer API_VERSION_MAJOR = 4;
    private static Integer API_VERSION_MINOR = 0;
    private Integer session_id = 123456789;

    public TRexClient(String host, String port, String userName) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        supportedCmds.add("api_sync");
        supportedCmds.add("get_supported_cmds");
        EtherType.register(QInQ);
    }

    @Override
    protected void serverAPISync() throws TRexConnectionException {
        LOGGER.info("Sync API with the TRex");

        Map<String, Object> apiVers = new HashMap<>();
        apiVers.put("type", "core");
        apiVers.put("major", API_VERSION_MAJOR);
        apiVers.put("minor", API_VERSION_MINOR);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("api_vers", Arrays.asList(apiVers));

        TRexClientResult<ApiVersion> result = callMethod("api_sync", parameters, ApiVersion.class);

        if (result.get() == null) {
            TRexConnectionException e = new TRexConnectionException(
                    "Unable to connect to TRex server. Required API version is " + API_VERSION_MAJOR + "."
                            + API_VERSION_MINOR);
            LOGGER.error("Unable to sync client with TRex server due to: API_H is null.", e.getMessage());
            throw e;
        }
        apiH = result.get().getApiH();
        LOGGER.info("Received api_H: {}", apiH);
    }

    // TODO: move to upper layer
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

    @Override
    public PortStatus acquirePort(int portIndex, Boolean force) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("session_id", session_id);
        payload.put("user", userName);
        payload.put("force", force);
        String json = callMethod("acquire", payload);
        JsonElement response = new JsonParser().parse(json);
        String handler = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsString();
        portHandlers.put(portIndex, handler);
        return getPortStatus(portIndex).get();
    }

    public void resetPort(int portIndex) {
        acquirePort(portIndex, true);
        stopTraffic(portIndex);
        removeAllStreams(portIndex);
        removeRxQueue(portIndex);
        serviceMode(portIndex, false);
        releasePort(portIndex);
    }

    private Map<String, Object> createPayload(int portIndex) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("port_id", portIndex);
        payload.put("api_h", apiH);
        String handler = portHandlers.get(portIndex);
        if (handler != null) {
            payload.put("handler", handler);
        }
        return payload;
    }

    @Override
    public PortStatus releasePort(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);

        payload.put("user", userName);
        callMethod("release", payload);
        portHandlers.remove(portIndex);
        return getPortStatus(portIndex).get();
    }

    public PortStatistics getPortStatistics(int portIndex) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("port_id", portIndex);
        return callMethod("get_port_stats", parameters, PortStatistics.class).get();
    }

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

    public PortStatus serviceMode(int portIndex, Boolean isOn) {
        LOGGER.info("Set service mode : {}", isOn ? "on" : "off");
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("enabled", isOn);
        callMethod("service", payload);
        return getPortStatus(portIndex).get();
    }

    public void addStream(int portIndex, Stream stream) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("stream_id", stream.getId());
        payload.put("stream", stream);
        callMethod("add_stream", payload);
    }

    public void addStream(int portIndex, int streamId, JsonObject stream) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("stream_id", streamId);
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
        return GSON.fromJson(stream, Stream.class);
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

    public List<Stream> getAllStreams(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        String json = callMethod("get_all_streams", payload);
        JsonElement response = new JsonParser().parse(json);
        JsonObject streams = response.getAsJsonArray().get(0)
                .getAsJsonObject().get("result")
                .getAsJsonObject().get("streams")
                .getAsJsonObject();
        ArrayList<Stream> streamList = new ArrayList<>();

        for (Map.Entry<String, JsonElement> stream : streams.entrySet()) {
            streamList.add(GSON.fromJson(stream.getValue(), Stream.class));
        }
        return streamList;
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

    public void pauseStreams(int portIndex, List<Integer> streams) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("stream_ids", streams);
        callMethod("pause_streams", payload);
    }

    public void resumeStreams(int portIndex, List<Integer> streams) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("stream_ids", streams);
        callMethod("resume_streams", payload);
    }

    public void updateStreams(int portIndex, List<Integer> streams, boolean force,
            Map<String, Object> multiplier) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("force", force);
        payload.put("mul", multiplier);
        payload.put("stream_ids", streams);
        callMethod("update_streams", payload);
    }

    public ActivePGIds getActivePgids() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("pgids", "");
        return callMethod("get_active_pgids", parameters, ActivePGIdsRPCResult.class).get().getIds();
    }

    public PGIdStatsRPCResult getPgidStats(int[] ids) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("pgids", ids);
        return callMethod("get_pgid_stats", parameters, PGIdStatsRPCResult.class).get();
    }

    public void startTraffic(int portIndex, double duration, boolean force, Map<String, Object> mul, int coreMask) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("core_mask", coreMask);
        payload.put("mul", mul);
        payload.put("duration", duration);
        payload.put("force", force);
        callMethod("start_traffic", payload);
    }

    public void pauseTraffic(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        callMethod("pause_traffic", payload);
    }

    public void resumeTraffic(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        callMethod("resume_traffic", payload);
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
        payload.put("type", "queue");
        payload.put("enabled", false);
        callMethod("set_rx_feature", payload);
    }

    synchronized public void sendPacket(int portIndex, Packet pkt) {
        Stream stream = build1PktSingleBurstStream(pkt);

        removeAllStreams(portIndex);
        addStream(portIndex, stream);

        Map<String, Object> mul = new HashMap<>();
        mul.put("op", "abs");
        mul.put("type", "pps");
        mul.put("value", 1.0);
        startTraffic(portIndex, 1, true, mul, 1);
    }

    synchronized public void startStreamsIntermediate(int portIndex, List<Stream> streams) {
        removeRxQueue(portIndex);
        setRxQueue(portIndex, 1000);
        removeAllStreams(portIndex);
        streams.forEach(s -> addStream(portIndex, s));

        Map<String, Object> mul = new HashMap<>();
        mul.put("op", "abs");
        mul.put("type", "percentage");
        mul.put("value", 100);
        startTraffic(portIndex, -1, true, mul, 1);
    }

    synchronized public void sendPackets(int portIndex, List<Packet> pkts) {
        removeAllStreams(portIndex);
        for (Packet pkt : pkts) {
            addStream(portIndex, build1PktSingleBurstStream(pkt));
        }

        Map<String, Object> mul = new HashMap<>();
        mul.put("op", "abs");
        mul.put("type", "pps");
        mul.put("value", 1.0);
        startTraffic(portIndex, 1, true, mul, 1);
        stopTraffic(portIndex);
    }

    public String resolveArp(int portIndex, String srcIp, String dstIp) {
        removeRxQueue(portIndex);
        setRxQueue(portIndex, 1000);

        String srcMac = getPortByIndex(portIndex).hw_mac;
        PortVlan vlan = getPortStatus(portIndex).get().getAttr().getVlan();
        EthernetPacket pkt = buildArpPkt(srcMac, srcIp, dstIp, vlan);
        sendPacket(portIndex, pkt);

        Predicate<EthernetPacket> arpReplyFilter = etherPkt -> {
            Queue<Integer> vlanTags = new LinkedList<>(vlan.getTags());
            Packet next_pkt = etherPkt;

            boolean vlanOutsideMatches = true;
            if (etherPkt.getHeader().getType() == QInQ) {
                try {
                    Dot1qVlanTagPacket QInQPkt = Dot1qVlanTagPacket.newPacket(etherPkt.getRawData(),
                            etherPkt.getHeader().length(), etherPkt.getPayload().length());
                    vlanOutsideMatches = QInQPkt.getHeader().getVidAsInt() == vlanTags.poll();

                    next_pkt = QInQPkt.getPayload();
                } catch (IllegalRawDataException e) {
                    return false;
                }
            }

            boolean vlanInsideMatches = true;
            if (next_pkt.contains(Dot1qVlanTagPacket.class)) {
                Dot1qVlanTagPacket dot1qVlanTagPacket = next_pkt.get(Dot1qVlanTagPacket.class);
                vlanInsideMatches = dot1qVlanTagPacket.getHeader().getVid() == vlanTags.poll();
            }

            if (next_pkt.contains(ArpPacket.class)) {
                ArpPacket arp = next_pkt.get(ArpPacket.class);
                ArpOperation arpOp = arp.getHeader().getOperation();
                String replyDstMac = arp.getHeader().getDstHardwareAddr().toString();
                boolean arpMatches = ArpOperation.REPLY.equals(arpOp) && replyDstMac.equals(srcMac);

                return arpMatches && vlanOutsideMatches && vlanInsideMatches;
            }
            return false;
        };

        List<org.pcap4j.packet.Packet> pkts = new ArrayList<>();

        try {
            int steps = 10;
            while (steps > 0) {
                steps -= 1;
                Thread.sleep(500);
                pkts.addAll(getRxQueue(portIndex, arpReplyFilter));
                if (!pkts.isEmpty()) {
                    ArpPacket arpPacket = getArpPkt(pkts.get(0));
                    if (arpPacket != null) {
                        return arpPacket.getHeader().getSrcHardwareAddr().toString();
                    }
                }
            }
            LOGGER.info("Unable to get ARP reply in {} seconds", steps);
        } catch (InterruptedException ignored) {} finally {
            removeRxQueue(portIndex);
            if (getPortStatus(portIndex).get().getState().equals("TX")) {
                stopTraffic(portIndex);
            }
            removeAllStreams(portIndex);
        }
        return null;
    }

    private static ArpPacket getArpPkt(Packet pkt) {
        if (pkt.contains(ArpPacket.class)) {
            return pkt.get(ArpPacket.class);
        }
        try {
            Dot1qVlanTagPacket unwrapedFromVlanPkt = Dot1qVlanTagPacket.newPacket(pkt.getRawData(),
                    pkt.getHeader().length(), pkt.getPayload().length());

            return unwrapedFromVlanPkt.get(ArpPacket.class);
        } catch (IllegalRawDataException ignored) {}

        return null;
    }

    private static EthernetPacket buildArpPkt(String srcMac, String srcIp, String dstIp, PortVlan vlan) {
        ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
        MacAddress srcMacAddress = MacAddress.getByName(srcMac);
        try {
            arpBuilder
                    .hardwareType(ArpHardwareType.ETHERNET)
                    .protocolType(EtherType.IPV4)
                    .hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
                    .protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES)
                    .operation(ArpOperation.REQUEST)
                    .srcHardwareAddr(srcMacAddress)
                    .srcProtocolAddr(InetAddress.getByName(srcIp))
                    .dstHardwareAddr(MacAddress.getByName("00:00:00:00:00:00"))
                    .dstProtocolAddr(InetAddress.getByName(dstIp));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }

        AbstractMap.SimpleEntry<EtherType, Packet.Builder> payload = new AbstractMap.SimpleEntry<>(EtherType.ARP,
                arpBuilder);
        if (!vlan.getTags().isEmpty()) {
            payload = buildVlan(arpBuilder, vlan);
        }

        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
                .srcAddr(srcMacAddress)
                .type(payload.getKey())
                .payloadBuilder(payload.getValue())
                .paddingAtBuild(true);

        return etherBuilder.build();
    }

    private static AbstractMap.SimpleEntry<EtherType, Packet.Builder> buildVlan(ArpPacket.Builder arpBuilder,
            PortVlan vlan) {
        Queue<Integer> vlanTags = new LinkedList<>(Lists.reverse(vlan.getTags()));
        Packet.Builder resultPayloadBuilder = arpBuilder;
        EtherType resultEtherType = EtherType.ARP;

        if (vlanTags.peek() != null) {
            Dot1qVlanTagPacket.Builder vlanInsideBuilder = new Dot1qVlanTagPacket.Builder();
            vlanInsideBuilder.type(EtherType.ARP)
                    .vid(vlanTags.poll().shortValue())
                    .payloadBuilder(arpBuilder);

            resultPayloadBuilder = vlanInsideBuilder;
            resultEtherType = EtherType.DOT1Q_VLAN_TAGGED_FRAMES;

            if (vlanTags.peek() != null) {
                Dot1qVlanTagPacket.Builder vlanOutsideBuilder = new Dot1qVlanTagPacket.Builder();
                vlanOutsideBuilder.type(EtherType.DOT1Q_VLAN_TAGGED_FRAMES)
                        .vid(vlanTags.poll().shortValue())
                        .payloadBuilder(vlanInsideBuilder);
                resultPayloadBuilder = vlanOutsideBuilder;
                resultEtherType = QInQ;
            }
        }

        return new AbstractMap.SimpleEntry<>(resultEtherType, resultPayloadBuilder);
    }

    private static Stream build1PktSingleBurstStream(Packet pkt) {
        int stream_id = (int) (Math.random() * 1000);
        return new Stream(
                stream_id,
                true,
                3,
                0.0,
                new StreamMode(
                        2,
                        2,
                        1,
                        1.0,
                        new StreamModeRate(
                                StreamModeRate.Type.pps,
                                1.0),
                        StreamMode.Type.single_burst),
                -1,
                pkt,
                new StreamRxStats(true, true, true, stream_id),
                new StreamVM("", Collections.<VMInstruction> emptyList()),
                true,
                false,
                null,
                -1);
    }

    public List<EthernetPacket> getRxQueue(int portIndex, Predicate<EthernetPacket> filter) {

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
            LOGGER.info("Received pkt: {}", pkt.toString());
            return pkt;
        } catch (IllegalRawDataException e) {
            return null;
        }
    }

    public boolean setL3Mode(int portIndex, String nextHopMac, String sourceIp, String destinationIp) {
        Map<String, Object> payload = createPayload(portIndex);
        payload.put("src_addr", sourceIp);
        payload.put("dst_addr", destinationIp);
        if (nextHopMac != null) {
            payload.put("resolved_mac", nextHopMac);
        }
        payload.put("block", false);
        callMethod("set_l3", payload);
        return true;
    }

    public void updatePortHandler(int portID, String handler) {
        portHandlers.put(portID, handler);
    }

    public void invalidatePortHandler(int portID) {
        portHandlers.remove(portID);
    }

    // TODO: move to upper layer
    public EthernetPacket sendIcmpEcho(int portIndex, String host, int reqId, int seqNumber, long waitResponse)
            throws UnknownHostException {
        Port port = getPortByIndex(portIndex);
        PortStatus portStatus = getPortStatus(portIndex).get();
        String srcIp = portStatus.getAttr().getLayerConiguration().getL3Configuration().getSrc();

        EthernetPacket icmpRequest = buildIcmpV4Request(port.hw_mac, port.dst_macaddr, srcIp, host, reqId, seqNumber);

        removeAllStreams(portIndex);
        setRxQueue(portIndex, 1000);
        sendPacket(portIndex, icmpRequest);
        try {
            Thread.sleep(waitResponse);
        } catch (InterruptedException ignored) {}

        try {
            List<EthernetPacket> receivedPkts = getRxQueue(portIndex,
                    etherPkt -> etherPkt.contains(IcmpV4EchoReplyPacket.class));
            if (!receivedPkts.isEmpty()) {
                return receivedPkts.get(0);
            }
            return null;
        } finally {
            removeRxQueue(portIndex);
        }
    }

    // TODO: move to upper layer
    private EthernetPacket buildIcmpV4Request(String srcMac, String dstMac, String srcIp, String dstIp, int reqId,
            int seqNumber) throws UnknownHostException {

        IcmpV4EchoPacket.Builder icmpReqBuilder = new IcmpV4EchoPacket.Builder();
        icmpReqBuilder.identifier((short) reqId);
        icmpReqBuilder.sequenceNumber((short) seqNumber);

        IcmpV4CommonPacket.Builder icmpv4CommonPacketBuilder = new IcmpV4CommonPacket.Builder();
        icmpv4CommonPacketBuilder.type(IcmpV4Type.ECHO)
                .code(IcmpV4Code.NO_CODE)
                .correctChecksumAtBuild(true)
                .payloadBuilder(icmpReqBuilder);

        IpV4Packet.Builder ipv4Builder = new IpV4Packet.Builder();
        ipv4Builder.version(IpVersion.IPV4)
                .tos(IpV4Rfc791Tos.newInstance((byte) 0))
                .ttl((byte) 64)
                .protocol(IpNumber.ICMPV4)
                .srcAddr((Inet4Address) Inet4Address.getByName(srcIp))
                .dstAddr((Inet4Address) Inet4Address.getByName(dstIp))
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true)
                .payloadBuilder(icmpv4CommonPacketBuilder);

        EthernetPacket.Builder eb = new EthernetPacket.Builder();
        eb.srcAddr(MacAddress.getByName(srcMac))
                .dstAddr(MacAddress.getByName(dstMac))
                .type(EtherType.IPV4)
                .paddingAtBuild(true)
                .payloadBuilder(ipv4Builder);

        return eb.build();
    }

    public void stopTraffic(int portIndex) {
        Map<String, Object> payload = createPayload(portIndex);
        callMethod("stop_traffic", payload);
    }

    public TRexClientResult<CaptureInfo[]> getActiveCaptures() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("command", "status");
        return callMethod("capture", payload, CaptureInfo[].class);
    }

    public TRexClientResult<CaptureMonitor> captureMonitorStart(
            List<Integer> rxPorts,
            List<Integer> txPorts,
            String filter) {
        return startCapture(rxPorts, txPorts, "cyclic", 100, filter);
    }

    public TRexClientResult<CaptureMonitor> captureRecorderStart(
            List<Integer> rxPorts,
            List<Integer> txPorts,
            String filter,
            int limit) {
        return startCapture(rxPorts, txPorts, "fixed", limit, filter);
    }

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

    public TRexClientResult<List<RPCResponse>> removeAllCaptures() {
        TRexClientResult<CaptureInfo[]> activeCaptures = getActiveCaptures();

        List<Integer> captureIds = Arrays.stream(activeCaptures.get()).map(CaptureInfo::getId)
                .collect(Collectors.toList());
        List<TRexCommand> commands = buildRemoveCaptureCommand(captureIds);
        return callMethods(commands);
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

    public TRexClientResult<CaptureMonitorStop> captureMonitorStop(int captureId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("command", "stop");
        payload.put("capture_id", captureId);

        return callMethod("capture", payload, CaptureMonitorStop.class);
    }

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

    public TRexClientResult<CapturedPackets> captureFetchPkts(int captureId, int chunkSize) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("command", "fetch");
        payload.put("capture_id", captureId);
        payload.put("pkt_limit", chunkSize);

        return callMethod("capture", payload, CapturedPackets.class);
    }

    public Map<String, Ipv6Node> scanIPv6(int portIndex) throws ServiceModeRequiredException {
        return new IPv6NeighborDiscoveryService(this).scan(portIndex, 10, null, null);
    }

    public EthernetPacket sendIcmpV6Echo(int portIndex, String dstIp, int icmpId, int icmpSeq, int timeOut)
            throws ServiceModeRequiredException {
        return new IPv6NeighborDiscoveryService(this).sendIcmpV6Echo(portIndex, dstIp, icmpId, icmpSeq, timeOut);
    }

    public TRexClientResult<StubResult> setVlan(int portIdx, List<Integer> vlanIds) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("port_id", portIdx);
        parameters.put("vlan", vlanIds);
        parameters.put("block", false);

        return callMethod("set_vlan", parameters, StubResult.class);
    }
}
