package com.cisco.trex.stateless;

import static org.pcap4j.util.ByteArrays.BYTE_SIZE_IN_BYTES;

import com.cisco.trex.stateless.exception.ServiceModeRequiredException;
import com.cisco.trex.stateless.model.Ipv6Node;
import com.cisco.trex.stateless.model.PortStatus;
import com.cisco.trex.stateless.model.StreamMode;
import com.cisco.trex.stateless.model.StreamModeRate;
import com.cisco.trex.stateless.model.StreamRxStats;
import com.cisco.trex.stateless.model.StreamVM;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.stateless.model.port.PortVlan;
import com.cisco.trex.stateless.model.vm.FixChecksumHw;
import com.cisco.trex.stateless.model.vm.FixChecksumHw.L4Type;
import com.cisco.trex.stateless.model.vm.VMInstruction;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.pcap4j.packet.Dot1qVlanTagPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV6CommonPacket;
import org.pcap4j.packet.IcmpV6CommonPacket.IpV6NeighborDiscoveryOption;
import org.pcap4j.packet.IcmpV6EchoReplyPacket;
import org.pcap4j.packet.IcmpV6EchoRequestPacket;
import org.pcap4j.packet.IcmpV6NeighborAdvertisementPacket;
import org.pcap4j.packet.IcmpV6NeighborAdvertisementPacket.IcmpV6NeighborAdvertisementHeader;
import org.pcap4j.packet.IcmpV6NeighborSolicitationPacket;
import org.pcap4j.packet.IpV6NeighborDiscoverySourceLinkLayerAddressOption;
import org.pcap4j.packet.IpV6NeighborDiscoveryTargetLinkLayerAddressOption;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.IpV6SimpleFlowLabel;
import org.pcap4j.packet.IpV6SimpleTrafficClass;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IcmpV6Code;
import org.pcap4j.packet.namednumber.IcmpV6Type;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

public class IPv6NeighborDiscoveryService {

  private static final EtherType QInQ =
      new EtherType((short) 0x88a8, "802.1Q Provider Bridge (Q-in-Q)");
  private static final int L3LENGTH = 40;
  private static final int L2LENGTH = 14;
  private TRexClient tRexClient;

  public IPv6NeighborDiscoveryService(TRexClient tRexClient) {
    this.tRexClient = tRexClient;
  }

  public Map<String, Ipv6Node> scan(int portIdx, int timeDuration, String dstIP, String srcIP)
      throws ServiceModeRequiredException {
    String broadcastIP = "ff02::1";

    long endTimeSec = System.currentTimeMillis() + timeDuration / 2 * 1000;
    TRexClientResult<PortStatus> portStatusResult = tRexClient.getPortStatus(portIdx);
    PortStatus portStatus = portStatusResult.get();

    if (!portStatus.getService()) {
      throw new ServiceModeRequiredException();
    }

    String srcMac = portStatus.getAttr().getLayerConiguration().getL2Configuration().getSrc();
    PortVlan vlan = portStatus.getAttr().getVlan();

    Packet pingPkt =
        buildICMPV6EchoReq(
            srcIP,
            srcMac,
            multicastMacFromIPv6(broadcastIP).toString(),
            expandIPv6Address(broadcastIP));
    tRexClient.startStreamsIntermediate(portIdx, Collections.singletonList(buildStream(pingPkt)));

    List<com.cisco.trex.stateless.model.Stream> nsNaStreams = new ArrayList<>();
    Predicate<EthernetPacket> ipV6NSPktFilter =
        etherPkt ->
            etherPkt.contains(IcmpV6NeighborSolicitationPacket.class)
                || etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class);

    while (endTimeSec > System.currentTimeMillis()) {
      tRexClient
          .getRxQueue(portIdx, ipV6NSPktFilter)
          .forEach(
              pkt -> {
                IpV6Packet ipV6Packet = pkt.get(IpV6Packet.class);
                String nodeIp = ipV6Packet.getHeader().getSrcAddr().toString().substring(1);
                String nodeMac = getLinkLayerAddress(ipV6Packet);

                nsNaStreams.add(
                    buildStream(buildICMPV6NSPkt(vlan, srcMac, nodeMac, nodeIp, srcIP)));
                nsNaStreams.add(
                    buildStream(buildICMPV6NAPkt(vlan, srcMac, nodeMac, nodeIp, srcIP)));
              });
    }

    tRexClient.startStreamsIntermediate(portIdx, nsNaStreams);

    List<EthernetPacket> icmpNAReplies = new ArrayList<>();

    Predicate<EthernetPacket> ipV6NAPktFilter =
        etherPkt -> etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class);
    endTimeSec = System.currentTimeMillis() + timeDuration / 2 * 1000;
    while (endTimeSec > System.currentTimeMillis()) {
      icmpNAReplies.addAll(tRexClient.getRxQueue(portIdx, ipV6NAPktFilter));
    }
    tRexClient.removeRxQueue(portIdx);
    return icmpNAReplies
        .stream()
        .map(this::toIpv6Node)
        .distinct()
        .filter(
            ipv6Node -> {
              if (dstIP != null) {
                return InetAddresses.forString(dstIP)
                    .equals(InetAddresses.forString(ipv6Node.getIp()));
              }
              return true;
            })
        .collect(Collectors.toMap(Ipv6Node::getIp, node -> node));
  }

  private Ipv6Node toIpv6Node(EthernetPacket ethernetPacket) {
    IcmpV6NeighborAdvertisementHeader icmpV6NaHdr =
        ethernetPacket.get(IcmpV6NeighborAdvertisementPacket.class).getHeader();
    boolean isRouter = icmpV6NaHdr.getRouterFlag();
    String nodeIp = icmpV6NaHdr.getTargetAddress().toString().substring(1);
    String nodeMac = ethernetPacket.getHeader().getSrcAddr().toString();
    return new Ipv6Node(nodeMac, nodeIp, isRouter);
  }

  public EthernetPacket sendIcmpV6Echo(
      int portIdx, String dstIp, int icmpId, int icmpSeq, int timeOut)
      throws ServiceModeRequiredException {
    PortStatus portStatus = tRexClient.getPortStatus(portIdx).get();
    if (!portStatus.getService()) {
      throw new ServiceModeRequiredException();
    }
    String srcMac = portStatus.getAttr().getLayerConiguration().getL2Configuration().getSrc();
    return sendIcmpV6Echo(portIdx, srcMac, dstIp, icmpId, icmpSeq, timeOut);
  }

  public EthernetPacket sendIcmpV6Echo(
      int portIdx, String srcMac, String dstIp, int icmpId, int icmpSeq, int timeOut) {
    Map<String, EthernetPacket> stringEthernetPacketMap =
        sendNSandIcmpV6Req(portIdx, timeOut, srcMac, dstIp);

    Optional<Map.Entry<String, EthernetPacket>> icmpMulticastResponse =
        stringEthernetPacketMap.entrySet().stream().findFirst();

    EthernetPacket icmpUnicastReply = null;

    if (icmpMulticastResponse.isPresent()) {
      EthernetPacket etherPkt = icmpMulticastResponse.get().getValue();
      String nodeMac = etherPkt.getHeader().getSrcAddr().toString();
      Packet pingPkt = buildICMPV6EchoReq(null, srcMac, nodeMac, dstIp, icmpId, icmpSeq);
      tRexClient.startStreamsIntermediate(portIdx, Arrays.asList(buildStream(pingPkt)));
      long endTimeSec = System.currentTimeMillis() + timeOut * 1000 / 2;

      while (endTimeSec > System.currentTimeMillis()) {
        List<EthernetPacket> rxQueue =
            tRexClient.getRxQueue(portIdx, pkt -> pkt.contains(IcmpV6EchoReplyPacket.class));
        if (!rxQueue.isEmpty()) {
          icmpUnicastReply = rxQueue.get(0);
        }
      }
    }

    tRexClient.removeRxQueue(portIdx);
    if (tRexClient.getPortStatus(portIdx).get().getState().equals("TX")) {
      tRexClient.stopTraffic(portIdx);
    }
    tRexClient.removeAllStreams(portIdx);
    return icmpUnicastReply;
  }

  public EthernetPacket sendNeighborSolicitation(int portIdx, int timeout, String dstIp)
      throws ServiceModeRequiredException {
    PortStatus portStatus = tRexClient.getPortStatus(portIdx).get();
    if (!portStatus.getService()) {
      throw new ServiceModeRequiredException();
    }
    String srcMac = portStatus.getAttr().getLayerConiguration().getL2Configuration().getSrc();
    PortVlan vlan = portStatus.getAttr().getVlan();
    return sendNeighborSolicitation(vlan, portIdx, timeout, srcMac, null, null, dstIp);
  }

  public EthernetPacket sendNeighborSolicitation(
      PortVlan vlan,
      int portIdx,
      int timeout,
      String srcMac,
      String dstMac,
      String srcIp,
      String dstIp) {
    long endTs = System.currentTimeMillis() + timeout * 1000;

    if (dstMac == null) {
      dstMac = multicastMacFromIPv6(dstIp).toString();
    }

    Packet icmpv6NSPkt = buildICMPV6NSPkt(vlan, srcMac, dstMac, dstIp, srcIp);
    List<VMInstruction> instructions = new ArrayList<>();
    int layer2Length = L2LENGTH;
    if (!vlan.getTags().isEmpty()) {
      layer2Length = 18;
    }
    instructions.add(new FixChecksumHw(layer2Length, L3LENGTH, L4Type.IP));
    tRexClient.startStreamsIntermediate(
        portIdx, Arrays.asList(buildStream(icmpv6NSPkt, instructions)));

    Predicate<EthernetPacket> ipV6NAPktFilter =
        etherPkt -> {
          if (!etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class)) {
            return false;
          }
          IcmpV6NeighborAdvertisementHeader icmpV6NaHdr =
              etherPkt.get(IcmpV6NeighborAdvertisementPacket.class).getHeader();

          String nodeIp = icmpV6NaHdr.getTargetAddress().toString().substring(1);

          IpV6Packet.IpV6Header ipV6Header = etherPkt.get(IpV6Packet.class).getHeader();
          String dstAddr = ipV6Header.getDstAddr().toString().substring(1);

          try {
            Inet6Address dstIPv6Addr = (Inet6Address) InetAddress.getByName(dstAddr);
            Inet6Address srcIPv6Addr =
                (Inet6Address) InetAddress.getByName(generateIPv6AddrFromMAC(srcMac));

            Inet6Address nodeIpv6 = (Inet6Address) InetAddress.getByName(nodeIp);
            Inet6Address targetIpv6inNS = (Inet6Address) InetAddress.getByName(dstIp);
            return icmpV6NaHdr.getSolicitedFlag()
                && nodeIpv6.equals(targetIpv6inNS)
                && dstIPv6Addr.equals(srcIPv6Addr);
          } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid address", e);
          }
        };

    EthernetPacket na = null;
    while (endTs > System.currentTimeMillis() && na == null) {
      List<EthernetPacket> pkts = tRexClient.getRxQueue(portIdx, ipV6NAPktFilter);
      if (!pkts.isEmpty()) {
        na = pkts.get(0);
      }
    }
    tRexClient.removeRxQueue(portIdx);
    if (tRexClient.getPortStatus(portIdx).get().getState().equals("TX")) {
      tRexClient.stopTraffic(portIdx);
    }
    tRexClient.removeAllStreams(portIdx);
    return na;
  }

  private static AbstractMap.SimpleEntry<EtherType, Packet.Builder> buildVlan(
      IpV6Packet.Builder ipv6Builder, PortVlan vlan) {
    Queue<Integer> vlanTags = new LinkedList<>(Lists.reverse(vlan.getTags()));
    Packet.Builder resultPayloadBuilder = ipv6Builder;
    EtherType resultEtherType = EtherType.IPV6;

    if (vlanTags.peek() != null) {
      Dot1qVlanTagPacket.Builder vlanInsideBuilder = new Dot1qVlanTagPacket.Builder();
      vlanInsideBuilder
          .type(EtherType.IPV6)
          .vid(vlanTags.poll().shortValue())
          .payloadBuilder(ipv6Builder);

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

  private Map<String, EthernetPacket> sendNSandIcmpV6Req(
      int portIdx, int timeDuration, String srcMac, String dstIp) {
    long endTs = System.currentTimeMillis() + timeDuration * 1000;
    TRexClientResult<PortStatus> portStatusResult = tRexClient.getPortStatus(portIdx);
    PortVlan vlan = portStatusResult.get().getAttr().getVlan();

    Packet pingPkt = buildICMPV6EchoReq(null, srcMac, null, dstIp);
    Packet icmpv6NSPkt =
        buildICMPV6NSPkt(vlan, srcMac, multicastMacFromIPv6(dstIp).toString(), dstIp, null);

    List<com.cisco.trex.stateless.model.Stream> stlStreams =
        Stream.of(buildStream(pingPkt), buildStream(icmpv6NSPkt)).collect(Collectors.toList());

    tRexClient.startStreamsIntermediate(portIdx, stlStreams);

    Map<String, EthernetPacket> naIncomingRequests = new HashMap<>();

    Predicate<EthernetPacket> ipV6NAPktFilter =
        etherPkt -> {
          if (!etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class)) {
            return false;
          }
          IcmpV6NeighborAdvertisementHeader icmpV6NaHdr =
              etherPkt.get(IcmpV6NeighborAdvertisementPacket.class).getHeader();

          String nodeIp = icmpV6NaHdr.getTargetAddress().toString().substring(1);

          IpV6Packet.IpV6Header ipV6Header = etherPkt.get(IpV6Packet.class).getHeader();
          String dstAddr = ipV6Header.getDstAddr().toString().substring(1);

          try {
            Inet6Address dstIPv6Addr = (Inet6Address) InetAddress.getByName(dstAddr);
            Inet6Address srcIPv6Addr =
                (Inet6Address) InetAddress.getByName(generateIPv6AddrFromMAC(srcMac));
            return !naIncomingRequests.containsKey(nodeIp) && dstIPv6Addr.equals(srcIPv6Addr);
          } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid address", e);
          }
        };

    while (endTs > System.currentTimeMillis()) {
      tRexClient
          .getRxQueue(portIdx, ipV6NAPktFilter)
          .forEach(
              pkt -> {
                IcmpV6NeighborAdvertisementHeader icmpV6NaHdr =
                    pkt.get(IcmpV6NeighborAdvertisementPacket.class).getHeader();
                String nodeIp = icmpV6NaHdr.getTargetAddress().toString().substring(1);
                naIncomingRequests.put(nodeIp, pkt);
              });
    }
    tRexClient.removeRxQueue(portIdx);
    return naIncomingRequests;
  }

  private static com.cisco.trex.stateless.model.Stream buildStream(Packet pkt) {
    return buildStream(pkt, Collections.emptyList());
  }

  private static com.cisco.trex.stateless.model.Stream buildStream(
      Packet pkt, List<VMInstruction> instructions) {
    int streamId = (int) (Math.random() * 1000);
    return new com.cisco.trex.stateless.model.Stream(
        streamId,
        true,
        3,
        0.0,
        new StreamMode(
            2,
            2,
            5,
            1.0,
            new StreamModeRate(StreamModeRate.Type.percentage, 100.0),
            StreamMode.Type.single_burst),
        -1,
        pkt,
        new StreamRxStats(false, false, true, streamId),
        new StreamVM("", instructions),
        true,
        false,
        null);
  }

  private static Packet buildICMPV6NSPkt(
      PortVlan vlan, String srcMac, String dstMac, String dstIp, String srcIp) {
    EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
    try {

      IpV6NeighborDiscoverySourceLinkLayerAddressOption sourceLLAddr =
          new IpV6NeighborDiscoverySourceLinkLayerAddressOption.Builder()
              .correctLengthAtBuild(true)
              .linkLayerAddress(hexStringToByteArray(srcMac.replace(":", "")))
              .build();

      IcmpV6NeighborSolicitationPacket.Builder ipv6NSBuilder =
          new IcmpV6NeighborSolicitationPacket.Builder();
      ipv6NSBuilder
          .options(Arrays.asList(sourceLLAddr))
          .targetAddress((Inet6Address) InetAddress.getByName(dstIp));

      final String specifiedSrcIP = srcIp != null ? srcIp : generateIPv6AddrFromMAC(srcMac);

      IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
      icmpCommonPktBuilder
          .srcAddr((Inet6Address) InetAddress.getByName(specifiedSrcIP))
          .dstAddr((Inet6Address) InetAddress.getByName(dstIp))
          .type(IcmpV6Type.NEIGHBOR_SOLICITATION)
          .code(IcmpV6Code.NO_CODE)
          .correctChecksumAtBuild(true)
          .payloadBuilder(ipv6NSBuilder);

      // Calculate the Solicited-Node multicast address, RFC 4291 chapter 2.7.1
      String[] destIpParts = dstIp.split(":");
      String multicastIp =
          String.format(
              "FF02::1:FF%s:%s", destIpParts[6].substring(2, 4), destIpParts[7].substring(0, 4));

      IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
      ipV6Builder
          .srcAddr((Inet6Address) InetAddress.getByName(specifiedSrcIP))
          .dstAddr((Inet6Address) InetAddress.getByName(multicastIp))
          .version(IpVersion.IPV6)
          .hopLimit((byte) -1)
          .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0))
          .flowLabel(IpV6SimpleFlowLabel.newInstance(0))
          .nextHeader(IpNumber.ICMPV6)
          .payloadBuilder(icmpCommonPktBuilder)
          .correctLengthAtBuild(true);

      AbstractMap.SimpleEntry<EtherType, Packet.Builder> payload =
          new AbstractMap.SimpleEntry<>(EtherType.IPV6, ipV6Builder);
      if (!vlan.getTags().isEmpty()) {
        payload = buildVlan(ipV6Builder, vlan);
      }

      ethBuilder
          .srcAddr(MacAddress.getByName(srcMac))
          .dstAddr(MacAddress.getByName(dstMac))
          .type(payload.getKey())
          .payloadBuilder(payload.getValue())
          .paddingAtBuild(true);

    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Invalid address", e);
    }
    return ethBuilder.build();
  }

  /**
   * IPv6 Neighbor Discovery Source Link Layer Address header
   *
   * <p>0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | Type | Length | Link-Layer
   * Address ... +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   */
  private static String getLinkLayerAddress(IpV6Packet pkt) {
    final int TYPE_OFFSET = 0;
    final int TYPE_SIZE = BYTE_SIZE_IN_BYTES;
    final int LENGTH_OFFSET = TYPE_OFFSET + TYPE_SIZE;
    final int LENGTH_SIZE = BYTE_SIZE_IN_BYTES;
    final int LINK_LAYER_ADDRESS_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;
    final int LINK_LAYER_ADDRESS_LENGTH = 6; // MAC address

    IcmpV6NeighborSolicitationPacket nsPkt = pkt.get(IcmpV6NeighborSolicitationPacket.class);

    IpV6NeighborDiscoveryOption linkLayerAddressOption = nsPkt.getHeader().getOptions().get(0);

    byte[] linkLayerAddress =
        ByteArrays.getSubArray(
            linkLayerAddressOption.getRawData(),
            LINK_LAYER_ADDRESS_OFFSET,
            LINK_LAYER_ADDRESS_LENGTH);

    return ByteArrays.toHexString(linkLayerAddress, ":");
  }

  private static Packet buildICMPV6NAPkt(
      PortVlan vlan, String srcMac, String dstMac, String dstIp, String srcIP) {
    final String specifiedSrcIP = srcIP != null ? srcIP : generateIPv6AddrFromMAC(srcMac);

    EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
    try {

      IpV6NeighborDiscoveryTargetLinkLayerAddressOption tLLAddr =
          new IpV6NeighborDiscoveryTargetLinkLayerAddressOption.Builder()
              .correctLengthAtBuild(true)
              .linkLayerAddress(hexStringToByteArray(dstMac.replace(":", "")))
              .build();

      IcmpV6NeighborAdvertisementPacket.Builder ipv6NABuilder =
          new IcmpV6NeighborAdvertisementPacket.Builder();
      ipv6NABuilder
          .routerFlag(false)
          .options(Arrays.asList(tLLAddr))
          .solicitedFlag(true)
          .overrideFlag(true)
          .targetAddress((Inet6Address) InetAddress.getByName(specifiedSrcIP));

      IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
      icmpCommonPktBuilder
          .srcAddr((Inet6Address) InetAddress.getByName(specifiedSrcIP))
          .dstAddr((Inet6Address) InetAddress.getByName(dstIp))
          .type(IcmpV6Type.NEIGHBOR_ADVERTISEMENT)
          .code(IcmpV6Code.NO_CODE)
          .correctChecksumAtBuild(true)
          .payloadBuilder(ipv6NABuilder);

      IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
      ipV6Builder
          .srcAddr((Inet6Address) InetAddress.getByName(specifiedSrcIP))
          .dstAddr((Inet6Address) InetAddress.getByName(dstIp))
          .version(IpVersion.IPV6)
          .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0))
          .flowLabel(IpV6SimpleFlowLabel.newInstance(0))
          .nextHeader(IpNumber.ICMPV6)
          .hopLimit((byte) 1)
          .payloadBuilder(icmpCommonPktBuilder)
          .correctLengthAtBuild(true);

      AbstractMap.SimpleEntry<EtherType, Packet.Builder> payload =
          new AbstractMap.SimpleEntry<>(EtherType.IPV6, ipV6Builder);
      if (!vlan.getTags().isEmpty()) {
        payload = buildVlan(ipV6Builder, vlan);
      }

      ethBuilder
          .srcAddr(MacAddress.getByName(srcMac))
          .dstAddr(MacAddress.getByName("33:33:00:00:00:01"))
          .type(payload.getKey())
          .payloadBuilder(payload.getValue())
          .paddingAtBuild(true);

    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Invalid address", e);
    }

    return ethBuilder.build();
  }

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] =
          (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

  public static EthernetPacket buildICMPV6EchoReq(
      String srcIp,
      String srcMacString,
      String dstMacString,
      String dstIp,
      int icmpId,
      int icmpSeq) {
    PortVlan vlan = new PortVlan();
    vlan.setTags(new ArrayList<Integer>());
    return buildICMPV6EchoReq(vlan, srcIp, srcMacString, dstMacString, dstIp, icmpId, icmpSeq);
  }

  public static EthernetPacket buildICMPV6EchoReq(
      PortVlan vlan,
      String srcIp,
      String srcMacString,
      String dstMacString,
      String dstIp,
      int icmpId,
      int icmpSeq) {
    /*
     *
     * mld_pkt = (Ether(src = self.src_mac, dst = self.dst_mld_mac) / IPv6(src = self.src_ip, dst =
     * self.dst_mld_ip, hlim = 1) / IPv6ExtHdrHopByHop(options = [RouterAlert(), PadN()]) /
     * ICMPv6MLReportV2() / MLDv2Addr(type = 4, len = 0, multicast_addr = 'ff02::2')) ping_pkt =
     * (Ether(src = self.src_mac, dst = dst_mac) / IPv6(src = self.src_ip, dst = self.dst_ip, hlim =
     * 1) / ICMPv6EchoRequest()) return [self.vlan.embed(mld_pkt), self.vlan.embed(ping_pkt)]
     */

    final String specifiedSrcIP = srcIp != null ? srcIp : generateIPv6AddrFromMAC(srcMacString);

    IcmpV6EchoRequestPacket.Builder icmpV6ERBuilder = new IcmpV6EchoRequestPacket.Builder();
    icmpV6ERBuilder.identifier((short) icmpId).sequenceNumber((short) icmpSeq);

    IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
    try {
      icmpCommonPktBuilder
          .srcAddr((Inet6Address) InetAddress.getByName(specifiedSrcIP))
          .dstAddr(
              (Inet6Address) InetAddress.getByName(dstIp != null ? dstIp : "ff02:0:0:0:0:0:0:1"))
          .type(IcmpV6Type.ECHO_REQUEST)
          .code(IcmpV6Code.NO_CODE)
          .correctChecksumAtBuild(true)
          .payloadBuilder(icmpV6ERBuilder);
      IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
      ipV6Builder
          .srcAddr((Inet6Address) InetAddress.getByName(specifiedSrcIP))
          .dstAddr(
              (Inet6Address) InetAddress.getByName(dstIp != null ? dstIp : "ff02:0:0:0:0:0:0:1"))
          .version(IpVersion.IPV6)
          .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0))
          .flowLabel(IpV6SimpleFlowLabel.newInstance(0))
          .nextHeader(IpNumber.ICMPV6)
          .hopLimit((byte) 64)
          .payloadBuilder(icmpCommonPktBuilder)
          .correctLengthAtBuild(true);

      MacAddress dstMac;
      if (dstMacString != null) {
        dstMac = MacAddress.getByName(dstMacString);
      } else {
        dstMac =
            dstIp == null ? MacAddress.getByName("33:33:00:00:00:01") : multicastMacFromIPv6(dstIp);
      }

      EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
      AbstractMap.SimpleEntry<EtherType, Packet.Builder> payload =
          new AbstractMap.SimpleEntry<>(EtherType.IPV6, ipV6Builder);
      if (!vlan.getTags().isEmpty()) {
        payload = buildVlan(ipV6Builder, vlan);
      }

      ethBuilder
          .srcAddr(MacAddress.getByName(srcMacString))
          .dstAddr(dstMac)
          .dstAddr(dstMac)
          .type(payload.getKey())
          .payloadBuilder(payload.getValue())
          .paddingAtBuild(true);

      return ethBuilder.build();
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Invalid address", e);
    }
  }

  public static EthernetPacket buildICMPV6EchoReq(
      String srcIp, String srcMacString, String dstMacString, String dstIp) {
    return buildICMPV6EchoReq(srcIp, srcMacString, dstMacString, dstIp, 0, 0);
  }

  /**
   * Convert to solicitated-node multicast described in RFC 2624 section 7
   *
   * @param ipV6
   * @return
   */
  static MacAddress multicastMacFromIPv6(String ipV6) {
    String expandedIPv6 = expandIPv6Address(ipV6);
    List<Long> ipv6Octets =
        Arrays.stream(expandedIPv6.split(":"))
            .map(octet -> Long.parseLong(octet, 16))
            .collect(Collectors.toList());

    int lastIdx = ipv6Octets.size() - 1;
    int preLastIdx = ipv6Octets.size() - 2;
    String macAddressStr =
        String.format(
            "33:33:ff:%02x:%02x:%02x",
            divMod(ipv6Octets.get(preLastIdx), 256)[1],
            divMod(ipv6Octets.get(lastIdx), 256)[0],
            divMod(ipv6Octets.get(lastIdx), 256)[1]);
    return MacAddress.getByName(macAddressStr);
  }

  private static long[] divMod(long a, long b) {
    long[] result = new long[2];
    result[1] = a % b;
    result[0] = (a - result[1]) / b;
    return result;
  }

  private static String expandIPv6Address(String shortAddress) {
    String[] addressArray = shortAddress.split(":");
    if (shortAddress.startsWith(":")) {
      addressArray[0] = "0";
    } else if (shortAddress.endsWith(":")) {
      addressArray[addressArray.length - 1] = "0";
    }

    for (int i = 0; i < addressArray.length; i++) {
      if (addressArray[i] == null || addressArray[i].isEmpty()) {
        StringBuilder sb = new StringBuilder();

        int leftSize = i + 1;
        String[] left = new String[i + 1];
        System.arraycopy(addressArray, 0, left, 0, leftSize);

        sb.append(Arrays.stream(left).collect(Collectors.joining(":")));

        String[] expanded =
            Stream.generate(() -> "0").limit(9 - addressArray.length).toArray(String[]::new);
        sb.append(Arrays.stream(expanded).collect(Collectors.joining(":")));
        sb.append(":");

        int rightSize = addressArray.length - i - 1;
        String[] right = new String[rightSize];
        System.arraycopy(addressArray, i + 1, right, 0, rightSize);
        sb.append(Arrays.stream(right).collect(Collectors.joining(":")));

        return sb.toString();
      }
    }
    return Arrays.stream(addressArray).collect(Collectors.joining(":"));
  }

  static String generateIPv6AddrFromMAC(String mac) {
    String prefix = "fe80";
    List<Integer> macOctets =
        Arrays.stream(mac.split(":"))
            .map(octet -> Integer.parseInt(octet, 16))
            .collect(Collectors.toList());
    // insert ff fe in the middle
    macOctets.add(3, 0xfe);
    macOctets.add(3, 0xff);

    // invert second bind
    macOctets.set(0, macOctets.get(0) ^ 0b1 << 1);
    List<String> strOctets = new ArrayList<>();
    for (int i = 0; i < macOctets.size(); i += 2) {
      strOctets.add(
          String.format(
              "%s%s",
              StringUtils.leftPad(Integer.toHexString(macOctets.get(i)), 2, "0"),
              StringUtils.leftPad(Integer.toHexString(macOctets.get(i + 1)), 2, "0")));
    }
    return String.format("%s::%s", prefix, String.join(":", strOctets));
  }
}
