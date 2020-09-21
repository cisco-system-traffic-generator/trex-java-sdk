package com.cisco.trex.stateless;

import com.cisco.trex.ClientBase;
import com.cisco.trex.stateless.exception.ServiceModeRequiredException;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.model.ApiVersionHandler;
import com.cisco.trex.stateless.model.Ipv6Node;
import com.cisco.trex.stateless.model.Port;
import com.cisco.trex.stateless.model.PortStatus;
import com.cisco.trex.stateless.model.Stream;
import com.cisco.trex.stateless.model.StreamMode;
import com.cisco.trex.stateless.model.StreamModeRate;
import com.cisco.trex.stateless.model.StreamRxStats;
import com.cisco.trex.stateless.model.StreamVM;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.stateless.model.port.PortVlan;
import com.cisco.trex.stateless.model.stats.ActivePGIds;
import com.cisco.trex.stateless.model.stats.ActivePGIdsRPCResult;
import com.cisco.trex.stateless.model.stats.PGIdStatsRPCResult;
import com.cisco.trex.stateless.model.vm.VMInstruction;
import com.cisco.trex.util.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
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

/** TRex client for stateless traffic */
public class TRexClient extends ClientBase {

  private static final String TYPE = "type";
  private static final String STREAM = "stream";
  private static final String STREAM_ID = "stream_id";
  private static final EtherType QInQ =
      new EtherType((short) 0x88a8, "802.1Q Provider Bridge (Q-in-Q)");
  private static final int SESSON_ID = 123456789;

  TRexClient(TRexTransport transport, Set<String> supportedCommands) {
    // For unit testing
    this.transport = transport;
    this.host = "testHost";
    this.port = "testPort";
    this.userName = "testUser";
    supportedCmds.addAll(supportedCommands);
  }

  /**
   * constructor
   *
   * @param host
   * @param port
   * @param userName
   */
  public TRexClient(String host, String port, String userName) {
    this.host = host;
    this.port = port;
    this.userName = userName;
    supportedCmds.add("api_sync_v2");
    supportedCmds.add("get_supported_cmds");
    EtherType.register(QInQ);
  }

  @Override
  protected void serverAPISync() throws TRexConnectionException {
    LOGGER.info("Sync API with the TRex");

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("name", "STL");
    parameters.put("major", Constants.STL_API_VERSION_MAJOR);
    parameters.put("minor", Constants.STL_API_VERSION_MINOR);

    TRexClientResult<ApiVersionHandler> result =
        callMethod("api_sync_v2", parameters, ApiVersionHandler.class);

    if (result.get() == null) {
      TRexConnectionException e =
          new TRexConnectionException(
              MessageFormat.format(
                  "Unable to connect to TRex server. Required API version is {0}.{1}. Error: {2}",
                  Constants.STL_API_VERSION_MAJOR,
                  Constants.STL_API_VERSION_MINOR,
                  result.getError()));
      LOGGER.error("Unable to sync client with TRex server due to: API_H is null.", e.getMessage());
      throw e;
    }
    apiH = result.get().getApiH();
    LOGGER.info("Received api_H: {}", apiH);
  }

  public PortStatus acquirePort(int portIndex, Boolean force) {
    if (!portHandlers.containsKey(portIndex)) {
      Map<String, Object> payload = createPayload(portIndex);
      payload.put("session_id", SESSON_ID);
      payload.put("user", userName);
      payload.put("force", force);
      String json = callMethod("acquire", payload);
      String handler = getResultFromResponse(json).getAsString();
      portHandlers.put(portIndex, handler);
    } else {
      LOGGER.debug("Port already acquired, continueing");
    }
    return getPortStatus(portIndex).get();
  }

  /**
   * Release Port
   *
   * @param portIndex
   * @return PortStatus
   */
  public PortStatus releasePort(int portIndex) {
    if (!portHandlers.containsKey(portIndex)) {
      LOGGER.debug("No handler assigned, port is not acquired.");
    } else {
      Map<String, Object> payload = createPayload(portIndex);
      payload.put("user", userName);
      String result = callMethod("release", payload);
      if (result.contains("must acquire the context")) {
        LOGGER.info("Port is not owned by this session, already released or never acquired");
      }
      portHandlers.remove(portIndex);
    }
    return getPortStatus(portIndex).get();
  }

  /**
   * Reset port stop traffic, remove all streams, remove rx queue, disable service mode and release
   * port
   *
   * @param portIndex
   */
  public void resetPort(int portIndex) {
    acquirePort(portIndex, true);
    stopTraffic(portIndex);
    for (String profileId : getProfileIds(portIndex)) {
      removeAllStreams(portIndex, profileId);
    }
    removeRxQueue(portIndex);
    serviceMode(portIndex, false);
    releasePort(portIndex);
  }

  /**
   * Set port in service mode, needed to be able to do arp resolution and packet captureing
   *
   * @param portIndex
   * @param isOn
   * @return PortStatus
   */
  public PortStatus serviceMode(int portIndex, Boolean isOn) {
    LOGGER.info("Set service mode : {}", isOn ? "on" : "off");
    Map<String, Object> payload = createPayload(portIndex);
    payload.put("enabled", isOn);
    callMethod("service", payload);
    return getPortStatus(portIndex).get();
  }

  public void addStream(int portIndex, Stream stream) {
    addStream(portIndex, "", stream.getId(), stream);
  }

  public void addStream(int portIndex, String profileId, Stream stream) {
    addStream(portIndex, profileId, stream.getId(), stream);
  }

  public void addStream(int portIndex, int streamId, JsonObject stream) {
    addStream(portIndex, "", streamId, (Object) stream);
  }

  public void addStream(int portIndex, String profileId, int streamId, JsonObject stream) {
    addStream(portIndex, profileId, streamId, stream);
  }

  private void addStream(int portIndex, String profileId, int streamId, Object streamObject) {
    Map<String, Object> payload = createPayload(portIndex, profileId);
    payload.put(STREAM_ID, streamId);
    payload.put(STREAM, streamObject);
    callMethod("add_stream", payload);
  }

  public Stream getStream(int portIndex, int streamId) {
    Map<String, Object> payload = createPayload(portIndex);
    payload.put("get_pkt", true);
    payload.put(STREAM_ID, streamId);

    String json = callMethod("get_stream", payload);
    JsonObject stream = getResultFromResponse(json).getAsJsonObject().get(STREAM).getAsJsonObject();
    return GSON.fromJson(stream, Stream.class);
  }

  public void removeStream(int portIndex, int streamId) {
    Map<String, Object> payload = createPayload(portIndex);
    payload.put(STREAM_ID, streamId);
    callMethod("remove_stream", payload);
  }

  public void removeStream(int portIndex, String profileId, int streamId) {
    Map<String, Object> payload = createPayload(portIndex, profileId);
    payload.put(STREAM_ID, streamId);
    callMethod("remove_stream", payload);
  }

  public void removeAllStreams(int portIndex) {
    removeAllStreams(portIndex, "");
  }

  public void removeAllStreams(int portIndex, String profileId) {
    Map<String, Object> payload = createPayload(portIndex, profileId);
    callMethod("remove_all_streams", payload);
  }

  public List<Stream> getAllStreams(int portIndex) {
    return getAllStreams(portIndex, "");
  }

  public List<Stream> getAllStreams(int portIndex, String profileId) {
    Map<String, Object> payload = createPayload(portIndex, profileId);
    String json = callMethod("get_all_streams", payload);
    JsonObject streams =
        getResultFromResponse(json).getAsJsonObject().get("streams").getAsJsonObject();
    ArrayList<Stream> streamList = new ArrayList<>();

    for (Map.Entry<String, JsonElement> stream : streams.entrySet()) {
      streamList.add(GSON.fromJson(stream.getValue(), Stream.class));
    }

    return streamList;
  }

  public List<Integer> getStreamIds(int portIndex) {
    return getStreamIds(portIndex, "");
  }

  public List<Integer> getStreamIds(int portIndex, String profileId) {
    Map<String, Object> payload = createPayload(portIndex, profileId);
    String json = callMethod("get_stream_list", payload);
    JsonArray ids = getResultFromResponse(json).getAsJsonArray();
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

  public void updateStreams(
      int portIndex, List<Integer> streams, boolean force, Map<String, Object> multiplier) {
    Map<String, Object> payload = createPayload(portIndex);
    payload.put("force", force);
    payload.put("mul", multiplier);
    payload.put("stream_ids", streams);
    callMethod("update_streams", payload);
  }

  public List<String> getProfileIds(int portIndex) {
    Map<String, Object> payload = createPayload(portIndex);
    String json = callMethod("get_profile_list", payload);
    JsonArray ids = getResultFromResponse(json).getAsJsonArray();
    return StreamSupport.stream(ids.spliterator(), false)
        .map(JsonElement::getAsString)
        .collect(Collectors.toList());
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

  public void startTraffic(
      int portIndex, double duration, boolean force, Map<String, Object> mul, long coreMask) {
    startTraffic(portIndex, "", duration, force, mul, coreMask);
  }

  public void startTraffic(
      int portIndex,
      String profileId,
      double duration,
      boolean force,
      Map<String, Object> mul,
      long coreMask) {
    Map<String, Object> payload = createPayload(portIndex, profileId);
    if (coreMask > 0) {
      payload.put("core_mask", coreMask);
    }
    payload.put("mul", mul);
    payload.put("duration", duration);
    payload.put("force", force);
    callMethod("start_traffic", payload);
  }

  public void startAllTraffic(
      int portIndex, double duration, boolean force, Map<String, Object> mul, long coreMask) {
    List<String> profileIds = getProfileIds(portIndex);
    for (String profileId : profileIds) {
      startTraffic(portIndex, profileId, duration, force, mul, coreMask);
    }
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
    payload.put(TYPE, "queue");
    payload.put("enabled", true);
    payload.put("size", size);
    callMethod("set_rx_feature", payload);
  }

  public void removeRxQueue(int portIndex) {
    Map<String, Object> payload = createPayload(portIndex);
    payload.put(TYPE, "queue");
    payload.put("enabled", false);
    callMethod("set_rx_feature", payload);
  }

  public void removeRxFilters(int portIndex, int profileId) {
    Map<String, Object> payload = createPayload(portIndex);
    if (profileId > 0) {
      payload.put("profile_id", profileId);
    }
    callMethod("remove_rx_filters", payload);
  }

  /**
   * Wait until traffic on specified port(s) has ended
   *
   * @param timeoutInSeconds
   * @param rxDelayMs Time to wait (in milliseconds) after last packet was sent, until RX filters
   *     used for measuring flow statistics and latency are removed. This value should reflect the
   *     time it takes packets which were transmitted to arrive to the destination. After this time,
   *     RX filters will be removed, and packets arriving for per flow statistics feature and
   *     latency flows will be counted as errors.
   * @param ports Ports on which to execute the command
   */
  public void waitOnTrafficToFinish(int timeoutInSeconds, int rxDelayMs, Port... ports) {
    long endTime = System.currentTimeMillis() + timeoutInSeconds * 1000;
    List<Port> portsStillSendingTraffic = new ArrayList<>(Arrays.asList(ports));

    while (!portsStillSendingTraffic.isEmpty()) {
      Iterator<Port> iter = portsStillSendingTraffic.iterator();
      while (iter.hasNext()) {
        if (getPortStatus(iter.next().getIndex()).get().getState() != "TX") {
          iter.remove();
        }
      }
      if (System.currentTimeMillis() > endTime) {
        break;
      }
      if (!portsStillSendingTraffic.isEmpty()) {
        sleepMilliSeconds(10);
      }
    }

    removeRxFiltersWithDelay(rxDelayMs, ports);
  }

  /**
   * Delay some time to let packets arrive at destination port before removing filters
   *
   * @param rxDelayMs
   * @param ports
   */
  protected void removeRxFiltersWithDelay(int rxDelayMs, Port... ports) {
    int rxDelayToUse;
    if (rxDelayMs <= 0) {
      if (ports[0].is_virtual) {
        rxDelayToUse = 100;
      } else {
        rxDelayToUse = 10;
      }
    } else {
      rxDelayToUse = rxDelayMs;
    }

    sleepMilliSeconds(rxDelayToUse);

    for (Port port : ports) {
      removeRxFilters(port.getIndex(), 0);
    }
  }

  protected void sleepMilliSeconds(int milliSeconds) {
    try {
      Thread.sleep(milliSeconds);
    } catch (InterruptedException e) {
      // Do nothing
    }
  }

  /** Set promiscuous mode, Enable interface to receive packets from all mac addresses */
  public void setPromiscuousMode(int portIndex, boolean enabled) {
    Map<String, Object> payload = createPayload(portIndex);
    Map<String, Object> attributes = new HashMap<>();
    Map<String, Object> promiscuousValue = new HashMap<>();
    promiscuousValue.put("enabled", enabled);
    attributes.put("promiscuous", promiscuousValue);
    payload.put("attr", attributes);
    callMethod("set_port_attr", payload);
  }

  /** Set flow control mode, Flow control: 0 = none, 1 = tx, 2 = rx, 3 = full */
  public void setFlowControlMode(int portIndex, int mode) {
    Map<String, Object> payload = createPayload(portIndex);
    Map<String, Object> attributes = new HashMap<>();
    Map<String, Object> flowCtrlValue = new HashMap<>();
    flowCtrlValue.put("mode", mode);
    attributes.put("flow_ctrl_mode", flowCtrlValue);
    payload.put("attr", attributes);
    callMethod("set_port_attr", payload);
  }

  public synchronized void sendPacket(int portIndex, Packet pkt) {
    Stream stream = build1PktSingleBurstStream(pkt);

    removeAllStreams(portIndex);
    addStream(portIndex, stream);

    Map<String, Object> mul = new HashMap<>();
    mul.put("op", "abs");
    mul.put(TYPE, "pps");
    mul.put("value", 1.0);
    startTraffic(portIndex, 1, true, mul, 1);
  }

  public synchronized void startStreamsIntermediate(int portIndex, List<Stream> streams) {
    removeRxQueue(portIndex);
    setRxQueue(portIndex, 1000);
    removeAllStreams(portIndex);
    streams.forEach(s -> addStream(portIndex, s));

    Map<String, Object> mul = new HashMap<>();
    mul.put("op", "abs");
    mul.put(TYPE, "pps");
    mul.put("value", 1.0);
    startTraffic(portIndex, -1, true, mul, 1);
  }

  public synchronized void sendPackets(int portIndex, List<Packet> pkts) {
    removeAllStreams(portIndex);
    for (Packet pkt : pkts) {
      addStream(portIndex, build1PktSingleBurstStream(pkt));
    }

    Map<String, Object> mul = new HashMap<>();
    mul.put("op", "abs");
    mul.put(TYPE, "pps");
    mul.put("value", 1.0);
    startTraffic(portIndex, 1, true, mul, 1);
    stopTraffic(portIndex);
  }

  public String resolveArp(int portIndex, String srcIp, String dstIp) {
    String srcMac = getPortByIndex(portIndex).hw_mac;
    PortVlan vlan = getPortStatus(portIndex).get().getAttr().getVlan();
    return resolveArp(portIndex, vlan, srcIp, srcMac, dstIp);
  }

  public String resolveArp(int portIndex, String srcIp, String srcMac, String dstIp) {
    PortVlan vlan = getPortStatus(portIndex).get().getAttr().getVlan();
    return resolveArp(portIndex, vlan, srcIp, srcMac, dstIp);
  }

  public String resolveArp(
      int portIndex, PortVlan vlan, String srcIp, String srcMac, String dstIp) {
    removeRxQueue(portIndex);
    setRxQueue(portIndex, 1000);

    EthernetPacket pkt = buildArpPkt(srcMac, srcIp, dstIp, vlan);
    sendPacket(portIndex, pkt);

    Predicate<EthernetPacket> arpReplyFilter =
        etherPkt -> {
          Queue<Integer> vlanTags = new LinkedList<>(vlan.getTags());
          Packet nextPkt = etherPkt;

          boolean vlanOutsideMatches = true;
          if (etherPkt.getHeader().getType() == QInQ) {
            try {
              Dot1qVlanTagPacket qInqPkt =
                  Dot1qVlanTagPacket.newPacket(
                      etherPkt.getRawData(),
                      etherPkt.getHeader().length(),
                      etherPkt.getPayload().length());
              vlanOutsideMatches = qInqPkt.getHeader().getVidAsInt() == vlanTags.poll();

              nextPkt = qInqPkt.getPayload();
            } catch (IllegalRawDataException e) {
              return false;
            }
          }

          boolean vlanInsideMatches = true;
          if (nextPkt.contains(Dot1qVlanTagPacket.class)) {
            Dot1qVlanTagPacket dot1qVlanTagPacket = nextPkt.get(Dot1qVlanTagPacket.class);
            vlanInsideMatches = dot1qVlanTagPacket.getHeader().getVid() == vlanTags.poll();
          }

          if (nextPkt.contains(ArpPacket.class)) {
            ArpPacket arp = nextPkt.get(ArpPacket.class);
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
    } catch (InterruptedException ignored) {
    } finally {
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
      Dot1qVlanTagPacket unwrapedFromVlanPkt =
          Dot1qVlanTagPacket.newPacket(
              pkt.getRawData(), pkt.getHeader().length(), pkt.getPayload().length());

      return unwrapedFromVlanPkt.get(ArpPacket.class);
    } catch (IllegalRawDataException ignored) {
    }

    return null;
  }

  private static EthernetPacket buildArpPkt(
      String srcMac, String srcIp, String dstIp, PortVlan vlan) {
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

    AbstractMap.SimpleEntry<EtherType, Packet.Builder> payload =
        new AbstractMap.SimpleEntry<>(EtherType.ARP, arpBuilder);
    if (!vlan.getTags().isEmpty()) {
      payload = buildVlan(arpBuilder, vlan);
    }

    EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
    etherBuilder
        .dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
        .srcAddr(srcMacAddress)
        .type(payload.getKey())
        .payloadBuilder(payload.getValue())
        .paddingAtBuild(true);

    return etherBuilder.build();
  }

  private static AbstractMap.SimpleEntry<EtherType, Packet.Builder> buildVlan(
      ArpPacket.Builder arpBuilder, PortVlan vlan) {
    List<Integer> tags = vlan.getTags();
    Collections.reverse(tags);
    Queue<Integer> vlanTags = new LinkedList<>(tags);
    Packet.Builder resultPayloadBuilder = arpBuilder;
    EtherType resultEtherType = EtherType.ARP;

    if (vlanTags.peek() != null) {
      Dot1qVlanTagPacket.Builder vlanInsideBuilder = new Dot1qVlanTagPacket.Builder();
      vlanInsideBuilder
          .type(EtherType.ARP)
          .vid(vlanTags.poll().shortValue())
          .payloadBuilder(arpBuilder);

      resultPayloadBuilder = vlanInsideBuilder;
      resultEtherType = EtherType.DOT1Q_VLAN_TAGGED_FRAMES;

      if (vlanTags.peek() != null) {
        Dot1qVlanTagPacket.Builder vlanOutsideBuilder = new Dot1qVlanTagPacket.Builder();
        vlanOutsideBuilder
            .type(EtherType.DOT1Q_VLAN_TAGGED_FRAMES)
            .vid(vlanTags.poll().shortValue())
            .payloadBuilder(vlanInsideBuilder);
        resultPayloadBuilder = vlanOutsideBuilder;
        resultEtherType = QInQ;
      }
    }

    return new AbstractMap.SimpleEntry<>(resultEtherType, resultPayloadBuilder);
  }

  private static Stream build1PktSingleBurstStream(Packet pkt) {
    int streamId = (int) (Math.random() * 1000);
    return new Stream(
        streamId,
        true,
        3,
        0.0,
        new StreamMode(
            2,
            2,
            1,
            1.0,
            new StreamModeRate(StreamModeRate.Type.pps, 1.0),
            StreamMode.Type.single_burst),
        -1,
        pkt,
        new StreamRxStats(true, true, true, streamId),
        new StreamVM("", Collections.<VMInstruction>emptyList()),
        true,
        false,
        null,
        -1);
  }

  public String resolveIpv6(int portIndex, String dstIp) throws ServiceModeRequiredException {
    removeRxQueue(portIndex);
    setRxQueue(portIndex, 1000);

    EthernetPacket naPacket =
        new IPv6NeighborDiscoveryService(this).sendNeighborSolicitation(portIndex, 5, dstIp);
    if (naPacket != null) {
      return naPacket.getHeader().getSrcAddr().toString();
    }

    return null;
  }

  public String resolveIpv6(
      PortVlan vlan, int portIndex, String srcMac, String srcIp, String dstIp) {
    removeRxQueue(portIndex);
    setRxQueue(portIndex, 1000);

    EthernetPacket naPacket =
        new IPv6NeighborDiscoveryService(this)
            .sendNeighborSolicitation(vlan, portIndex, 5, srcMac, null, srcIp, dstIp);
    if (naPacket != null) {
      return naPacket.getHeader().getSrcAddr().toString();
    }

    return null;
  }

  public List<EthernetPacket> getRxQueue(int portIndex, Predicate<EthernetPacket> filter) {
    Map<String, Object> payload = createPayload(portIndex);
    String json = callMethod("get_rx_queue_pkts", payload);
    JsonArray pkts = getResultFromResponse(json).getAsJsonObject().getAsJsonArray("pkts");
    return StreamSupport.stream(pkts.spliterator(), false)
        .map(this::buildEthernetPkt)
        .filter(filter)
        .collect(Collectors.toList());
  }

  private EthernetPacket buildEthernetPkt(JsonElement jsonElement) {
    try {
      byte[] binary =
          Base64.getDecoder().decode(jsonElement.getAsJsonObject().get("binary").getAsString());
      EthernetPacket pkt = EthernetPacket.newPacket(binary, 0, binary.length);
      LOGGER.info("Received pkt: {}", pkt.toString());
      return pkt;
    } catch (IllegalRawDataException e) {
      return null;
    }
  }

  public boolean setL3Mode(
      int portIndex, String nextHopMac, String sourceIp, String destinationIp) {
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
  public EthernetPacket sendIcmpEcho(
      int portIndex, String host, int reqId, int seqNumber, long waitResponse)
      throws UnknownHostException {
    Port port = getPortByIndex(portIndex);
    PortStatus portStatus = getPortStatus(portIndex).get();
    String srcIp = portStatus.getAttr().getLayerConiguration().getL3Configuration().getSrc();

    EthernetPacket icmpRequest =
        buildIcmpV4Request(port.hw_mac, port.dst_macaddr, srcIp, host, reqId, seqNumber);

    removeAllStreams(portIndex);
    setRxQueue(portIndex, 1000);
    sendPacket(portIndex, icmpRequest);
    try {
      Thread.sleep(waitResponse);
    } catch (InterruptedException ignored) {
    }

    try {
      List<EthernetPacket> receivedPkts =
          getRxQueue(portIndex, etherPkt -> etherPkt.contains(IcmpV4EchoReplyPacket.class));
      if (!receivedPkts.isEmpty()) {
        return receivedPkts.get(0);
      }
      return null;
    } finally {
      removeRxQueue(portIndex);
    }
  }

  // TODO: move to upper layer
  private static EthernetPacket buildIcmpV4Request(
      String srcMac, String dstMac, String srcIp, String dstIp, int reqId, int seqNumber)
      throws UnknownHostException {

    IcmpV4EchoPacket.Builder icmpReqBuilder = new IcmpV4EchoPacket.Builder();
    icmpReqBuilder.identifier((short) reqId);
    icmpReqBuilder.sequenceNumber((short) seqNumber);

    IcmpV4CommonPacket.Builder icmpv4CommonPacketBuilder = new IcmpV4CommonPacket.Builder();
    icmpv4CommonPacketBuilder
        .type(IcmpV4Type.ECHO)
        .code(IcmpV4Code.NO_CODE)
        .correctChecksumAtBuild(true)
        .payloadBuilder(icmpReqBuilder);

    IpV4Packet.Builder ipv4Builder = new IpV4Packet.Builder();
    ipv4Builder
        .version(IpVersion.IPV4)
        .tos(IpV4Rfc791Tos.newInstance((byte) 0))
        .ttl((byte) 64)
        .protocol(IpNumber.ICMPV4)
        .srcAddr((Inet4Address) InetAddress.getByName(srcIp))
        .dstAddr((Inet4Address) InetAddress.getByName(dstIp))
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
    stopTraffic(portIndex, "");
  }

  public void stopTraffic(int portIndex, String profileId) {
    Map<String, Object> payload = createPayload(portIndex, profileId);
    callMethod("stop_traffic", payload);
  }

  public void stopAllTraffic(int portIndex) {
    List<String> profileIds = getProfileIds(portIndex);
    for (String profileId : profileIds) {
      stopTraffic(portIndex, profileId);
    }
  }

  public Map<String, Ipv6Node> scanIPv6(int portIndex) throws ServiceModeRequiredException {
    return new IPv6NeighborDiscoveryService(this).scan(portIndex, 10, null, null);
  }

  public EthernetPacket sendIcmpV6Echo(
      int portIndex, String dstIp, int icmpId, int icmpSeq, int timeOut)
      throws ServiceModeRequiredException {
    return new IPv6NeighborDiscoveryService(this)
        .sendIcmpV6Echo(portIndex, dstIp, icmpId, icmpSeq, timeOut);
  }

  /** Set the link state: Up = true or Down = false of the given port. */
  public void setLinkState(int portIndex, boolean linkUp) {
    Map<String, Object> payload = createPayload(portIndex);
    Map<String, Object> attributes = new HashMap<>();
    Map<String, Object> linkValue = new HashMap<>();
    linkValue.put("up", linkUp);
    attributes.put("link_status", linkValue);
    payload.put("attr", attributes);
    callMethod("set_port_attr", payload);
  }
}
