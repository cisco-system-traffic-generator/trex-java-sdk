package com.cisco.trex.stateless;

import com.cisco.trex.stateless.exception.ServiceModeRequiredException;
import com.cisco.trex.stateless.model.*;
import com.google.common.net.InetAddresses;
import com.cisco.trex.stateless.model.Stream.RuleType;
import org.pcap4j.packet.*;
import org.pcap4j.packet.IcmpV6CommonPacket.IpV6NeighborDiscoveryOption;
import org.pcap4j.packet.IcmpV6NeighborAdvertisementPacket.IcmpV6NeighborAdvertisementHeader;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.pcap4j.util.ByteArrays.BYTE_SIZE_IN_BYTES;

public class IPv6NeighborDiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPv6NeighborDiscoveryService.class);

    private TRexClient tRexClient;
    private String srcMac;
    private long endTs;

    public IPv6NeighborDiscoveryService(TRexClient tRexClient) {
        this.tRexClient = tRexClient;
    }

    public Map<String, Ipv6Node> scan(int portIdx, int timeDuration, String dstIP, String srcIP) throws ServiceModeRequiredException {
        String broadcastIP = "ff02::1";

        long endTs = System.currentTimeMillis() + timeDuration/2 * 1000;
        TRexClientResult<PortStatus> portStatusResult = tRexClient.getPortStatus(portIdx);
        PortStatus portStatus = portStatusResult.get();

        if (!portStatus.getService()) {
            throw new ServiceModeRequiredException();
        }

        srcMac = portStatus.getAttr().getLayerConiguration().getL2Configuration().getSrc();

        Packet pingPkt = buildICMPV6EchoReq(srcIP, srcMac, multicastMacFromIPv6(broadcastIP).toString(), expandIPv6Address(broadcastIP));
        tRexClient.startStreamsIntermediate(portIdx, Collections.singletonList(buildStream(pingPkt)));

        List<com.cisco.trex.stateless.model.Stream> nsNaStreams = new ArrayList<>();
        Predicate<EthernetPacket> ipV6NSPktFilter = etherPkt -> etherPkt.contains(IcmpV6NeighborSolicitationPacket.class) || etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class);

        while (endTs > System.currentTimeMillis()) {
            tRexClient.getRxQueue(portIdx, ipV6NSPktFilter).forEach(pkt -> {
                IpV6Packet ipV6Packet = pkt.get(IpV6Packet.class);
                String nodeIp = ipV6Packet.getHeader().getSrcAddr().toString().substring(1);
                String nodeMac = getLinkLayerAddress(ipV6Packet);

                nsNaStreams.add(buildStream(buildICMPV6NSPkt(nodeMac, nodeIp, srcIP)));
                nsNaStreams.add(buildStream(buildICMPV6NAPkt(nodeMac, nodeIp, srcIP)));
            });
        }

        tRexClient.startStreamsIntermediate(portIdx, nsNaStreams);

        List<EthernetPacket> icmpNAReplies = new ArrayList<>();

        Predicate<EthernetPacket> ipV6NAPktFilter = etherPkt -> etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class);
        endTs = System.currentTimeMillis() + timeDuration/2 * 1000;
        while (endTs > System.currentTimeMillis()) {
            icmpNAReplies.addAll(tRexClient.getRxQueue(portIdx, ipV6NAPktFilter));
        }
        tRexClient.removeRxQueue(portIdx);
        return icmpNAReplies.stream()
                .map(this::toIpv6Node)
                .distinct()
                .filter(ipv6Node -> {
                    if(dstIP != null) {
                        return InetAddresses.forString(dstIP).equals(InetAddresses.forString(ipv6Node.getIp()));
                    }
                    return true;
                })
                .collect(Collectors.toMap(Ipv6Node::getIp, node -> node));
    }

    private Ipv6Node toIpv6Node(EthernetPacket ethernetPacket) {
        IcmpV6NeighborAdvertisementHeader icmpV6NaHdr = ethernetPacket.get(IcmpV6NeighborAdvertisementPacket.class).getHeader();
        boolean isRouter = icmpV6NaHdr.getRouterFlag();
        String nodeIp = icmpV6NaHdr.getTargetAddress().toString().substring(1);
        String nodeMac = ethernetPacket.getHeader().getSrcAddr().toString();
        return new Ipv6Node(nodeMac, nodeIp, isRouter);
    }

    public EthernetPacket sendIcmpV6Echo(int portIdx, String dstIp, int icmpId, int icmpSeq, int timeOut) throws ServiceModeRequiredException {
        Map<String, EthernetPacket> stringEthernetPacketMap = sendNSandIcmpV6Req(portIdx, timeOut, dstIp);

        Optional<Map.Entry<String, EthernetPacket>> icmpMulticastResponse = stringEthernetPacketMap.entrySet()
                .stream()
                .findFirst();

        EthernetPacket icmpUnicastReply = null;

        if (icmpMulticastResponse.isPresent()) {
            EthernetPacket etherPkt = icmpMulticastResponse.get().getValue();
            String nodeMac = etherPkt.getHeader().getSrcAddr().toString();
            Packet pingPkt = buildICMPV6EchoReq(null, srcMac, nodeMac, dstIp, icmpId, icmpSeq);
            tRexClient.startStreamsIntermediate(portIdx, Arrays.asList(buildStream(pingPkt)));
            long endTs = System.currentTimeMillis() + timeOut * 1000/2;

            while (endTs > System.currentTimeMillis()) {
                List<EthernetPacket> rxQueue = tRexClient.getRxQueue(portIdx, pkt -> pkt.contains(IcmpV6EchoReplyPacket.class));
                if (rxQueue.size() > 0) {
                    icmpUnicastReply = rxQueue.get(0);
                }
            }
        }
        tRexClient.removeRxQueue(portIdx);
        return icmpUnicastReply;

    }

    private Map<String, EthernetPacket> sendNSandIcmpV6Req(int portIdx, int timeDuration, String dstIp) throws ServiceModeRequiredException {
        endTs = System.currentTimeMillis() + timeDuration * 1000;
        TRexClientResult<PortStatus> portStatusResult = tRexClient.getPortStatus(portIdx);
        PortStatus portStatus = portStatusResult.get();

        srcMac = portStatus.getAttr().getLayerConiguration().getL2Configuration().getSrc();

        Packet pingPkt = buildICMPV6EchoReq(null, srcMac, null, dstIp);
        Packet icmpv6NSPkt = buildICMPV6NSPkt(multicastMacFromIPv6(dstIp).toString(), dstIp, null);

        List<com.cisco.trex.stateless.model.Stream> stlStreams = Stream.of(buildStream(pingPkt), buildStream(icmpv6NSPkt)).collect(Collectors.toList());

        tRexClient.startStreamsIntermediate(portIdx, stlStreams);

        Map<String, EthernetPacket> naIncomingRequests = new HashMap<>();

        Predicate<EthernetPacket> ipV6NAPktFilter = etherPkt -> {
            if (!etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class)) {
                return false;
            }
            IcmpV6NeighborAdvertisementHeader icmpV6NaHdr = etherPkt.get(IcmpV6NeighborAdvertisementPacket.class).getHeader();

            String nodeIp = icmpV6NaHdr.getTargetAddress().toString().substring(1);

            IpV6Packet.IpV6Header ipV6Header = etherPkt.get(IpV6Packet.class).getHeader();
            String dstAddr = ipV6Header.getDstAddr().toString().substring(1);

            try {
                Inet6Address dstIPv6Addr = (Inet6Address) Inet6Address.getByName(dstAddr);
                Inet6Address srcIPv6Addr = (Inet6Address) Inet6Address.getByName(generateIPv6AddrFromMAC(srcMac));
                return !naIncomingRequests.containsKey(nodeIp) && dstIPv6Addr.equals(srcIPv6Addr);
            } catch (UnknownHostException ignored) {}
            return false;
        };

        while (endTs > System.currentTimeMillis()) {
            tRexClient.getRxQueue(portIdx, ipV6NAPktFilter).forEach(pkt -> {
                IcmpV6NeighborAdvertisementHeader icmpV6NaHdr = pkt.get(IcmpV6NeighborAdvertisementPacket.class).getHeader();
                String nodeIp = icmpV6NaHdr.getTargetAddress().toString().substring(1);
                naIncomingRequests.put(nodeIp, pkt);
            });
        }
        tRexClient.removeRxQueue(portIdx);
        return naIncomingRequests;

    }

    private com.cisco.trex.stateless.model.Stream buildStream(Packet pkt) {
        int stream_id = (int) (Math.random() * 1000);
        return new com.cisco.trex.stateless.model.Stream(
                stream_id,
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
                new StreamRxStats(false, false, true, stream_id),
                new StreamVM("", Collections.emptyList()),
                true,
                false,
                null);
    }

    private Packet buildICMPV6NSPkt(String dstMac, String dstIp, String srcIp) {
        EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
        try {

            IpV6NeighborDiscoverySourceLinkLayerAddressOption sourceLLAddr 
                    = new IpV6NeighborDiscoverySourceLinkLayerAddressOption.Builder()
                    .correctLengthAtBuild(true)
                    .linkLayerAddress(hexStringToByteArray(srcMac.replace(":", "")))
                    .build();

            IcmpV6NeighborSolicitationPacket.Builder ipv6NSBuilder = new IcmpV6NeighborSolicitationPacket.Builder();
            ipv6NSBuilder
                    .options(Arrays.asList(sourceLLAddr))
                    .targetAddress((Inet6Address) Inet6Address.getByName(dstIp));

            final String specifiedSrcIP = srcIp != null ? srcIp : generateIPv6AddrFromMAC(srcMac);

            IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
            icmpCommonPktBuilder
                    .srcAddr((Inet6Address) Inet6Address.getByName(specifiedSrcIP))
                    .dstAddr((Inet6Address) Inet6Address.getByName(dstIp))
                    .type(IcmpV6Type.NEIGHBOR_SOLICITATION)
                    .code(IcmpV6Code.NO_CODE)
                    .correctChecksumAtBuild(true)
                    .payloadBuilder(ipv6NSBuilder);

            IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
            ipV6Builder
                    .srcAddr((Inet6Address) Inet6Address.getByName(specifiedSrcIP))
                    .dstAddr((Inet6Address) Inet6Address.getByName(dstIp))
                    .version(IpVersion.IPV6)
                    .hopLimit((byte) -1)
                    .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0))
                    .flowLabel(IpV6SimpleFlowLabel.newInstance(0))
                    .nextHeader(IpNumber.ICMPV6)
                    .payloadBuilder(icmpCommonPktBuilder)
                    .correctLengthAtBuild(true);

            ethBuilder
                    .type(EtherType.IPV6)
                    .srcAddr(MacAddress.getByName(srcMac))
                    .dstAddr(MacAddress.getByName(dstMac))
                    .payloadBuilder(ipV6Builder)
                    .paddingAtBuild(true);
        } catch (UnknownHostException ignored) {}
        return ethBuilder.build();
    }

    private static EthernetPacket buildIdealICMPV6NSPkt(String pkt) {
        byte[] pktBin = Base64.getDecoder().decode(pkt);
        try {
            return EthernetPacket.newPacket(pktBin, 0, pktBin.length);
        } catch (IllegalRawDataException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *   IPv6 Neighbor Discovery Source Link Layer Address header
     *
     *   0                   1                   2                   3
     *   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *  |     Type      |    Length     |    Link-Layer Address ...
     *  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */
    private String getLinkLayerAddress(IpV6Packet pkt) {
        final int TYPE_OFFSET = 0;
        final int TYPE_SIZE = BYTE_SIZE_IN_BYTES;
        final int LENGTH_OFFSET = TYPE_OFFSET + TYPE_SIZE;
        final int LENGTH_SIZE = BYTE_SIZE_IN_BYTES;
        final int LINK_LAYER_ADDRESS_OFFSET = LENGTH_OFFSET + LENGTH_SIZE;
        final int LINK_LAYER_ADDRESS_LENGTH = 6; // MAC address

        IcmpV6NeighborSolicitationPacket nsPkt = pkt.get(IcmpV6NeighborSolicitationPacket.class);

        IpV6NeighborDiscoveryOption linkLayerAddressOption = nsPkt.getHeader().getOptions().get(0);

        byte[] linkLayerAddress = ByteArrays.getSubArray(linkLayerAddressOption.getRawData(), LINK_LAYER_ADDRESS_OFFSET, LINK_LAYER_ADDRESS_LENGTH);

        return ByteArrays.toHexString(linkLayerAddress, ":");
    }

    private Packet buildICMPV6NAPkt(String dstMac, String dstIp, String srcIP) {
        final String specifiedSrcIP = srcIP != null ? srcIP : generateIPv6AddrFromMAC(srcMac);

        EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
        try {

            IpV6NeighborDiscoveryTargetLinkLayerAddressOption tLLAddr 
                = new IpV6NeighborDiscoveryTargetLinkLayerAddressOption.Builder()
                    .correctLengthAtBuild(true)
                    .linkLayerAddress(hexStringToByteArray(dstMac.replace(":", "")))
                    .build();

            IcmpV6NeighborAdvertisementPacket.Builder ipv6NABuilder = new IcmpV6NeighborAdvertisementPacket.Builder();
            ipv6NABuilder.routerFlag(false)
                    .options(Arrays.asList(tLLAddr))
                    .solicitedFlag(true)
                    .overrideFlag(true)
                    .targetAddress((Inet6Address) Inet6Address.getByName(specifiedSrcIP));

            IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
            icmpCommonPktBuilder
                    .srcAddr((Inet6Address) Inet6Address.getByName(specifiedSrcIP))
                    .dstAddr((Inet6Address) Inet6Address.getByName(dstIp))
                    .type(IcmpV6Type.NEIGHBOR_ADVERTISEMENT)
                    .code(IcmpV6Code.NO_CODE)
                    .correctChecksumAtBuild(true)
                    .payloadBuilder(ipv6NABuilder);

            IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
            ipV6Builder
                    .srcAddr((Inet6Address) Inet6Address.getByName(specifiedSrcIP))
                    .dstAddr((Inet6Address) Inet6Address.getByName(dstIp))
                    .version(IpVersion.IPV6)
                    .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0))
                    .flowLabel(IpV6SimpleFlowLabel.newInstance(0))
                    .nextHeader(IpNumber.ICMPV6)
                    .hopLimit((byte) 1)
                    .payloadBuilder(icmpCommonPktBuilder)
                    .correctLengthAtBuild(true);

            ethBuilder
                    .type(EtherType.IPV6)
                    .srcAddr(MacAddress.getByName(srcMac))
                    .dstAddr(MacAddress.getByName("33:33:00:00:00:01"))
                    .payloadBuilder(ipV6Builder)
                    .paddingAtBuild(true);
        } catch (UnknownHostException ignored) {}

        return ethBuilder.build();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static EthernetPacket buildICMPV6EchoReq(String srcIp, String srcMacString, String dstMacString, String dstIp, int icmpId, int icmpSeq) {
        final String specifiedSrcIP = srcIp != null ? srcIp : generateIPv6AddrFromMAC(srcMacString);

        IcmpV6EchoRequestPacket.Builder icmpV6ERBuilder = new IcmpV6EchoRequestPacket.Builder();
        icmpV6ERBuilder
                .identifier((short) icmpId)
                .sequenceNumber((short) icmpSeq);

        IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
        try {
            icmpCommonPktBuilder
                    .srcAddr((Inet6Address) Inet6Address.getByName(specifiedSrcIP))
                    .dstAddr((Inet6Address) Inet6Address.getByName(dstIp != null ? dstIp : "ff02:0:0:0:0:0:0:1"))
                    .type(IcmpV6Type.ECHO_REQUEST)
                    .code(IcmpV6Code.NO_CODE)
                    .correctChecksumAtBuild(true)
                    .payloadBuilder(icmpV6ERBuilder);
            IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
            ipV6Builder
                    .srcAddr((Inet6Address) Inet6Address.getByName(specifiedSrcIP))
                    .dstAddr((Inet6Address) Inet6Address.getByName(dstIp != null ? dstIp : "ff02:0:0:0:0:0:0:1"))
                    .version(IpVersion.IPV6)
                    .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0))
                    .flowLabel(IpV6SimpleFlowLabel.newInstance(0))
                    .nextHeader(IpNumber.ICMPV6)
                    .hopLimit((byte) 64)
                    .payloadBuilder(icmpCommonPktBuilder)
                    .correctLengthAtBuild(true);

            MacAddress dstMac = null;
            if (dstMacString != null) {
                dstMac = MacAddress.getByName(dstMacString);
            } else {
                dstMac = dstIp == null ? MacAddress.getByName("33:33:00:00:00:01") : multicastMacFromIPv6(dstIp);
            }

            EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
            ethBuilder
                    .type(EtherType.IPV6)
                    .srcAddr(MacAddress.getByName(srcMacString))
                    .dstAddr(dstMac)
                    .payloadBuilder(ipV6Builder)
                    .paddingAtBuild(true);

            return ethBuilder.build();
        } catch (UnknownHostException ignore) {}
        return null;
    }

    public static EthernetPacket buildICMPV6EchoReq(String srcIp, String srcMacString, String dstMacString, String dstIp) {
        return buildICMPV6EchoReq(srcIp, srcMacString, dstMacString, dstIp, 0, 0);
    }

    private static MacAddress multicastMacFromIPv6(String ipV6) {
        String expandedIPv6 = expandIPv6Address(ipV6);
        List<Long> ipv6Octets = Arrays.stream(expandedIPv6.split(":"))
                .map(octet -> Long.parseLong(octet, 16))
                .collect(Collectors.toList());

        int lastIdx = ipv6Octets.size() - 1;
        int preLastIdx = ipv6Octets.size() - 2;
        String macAddressStr = String.format("33:33:%02x:%02x:%02x:%02x", divMod(ipv6Octets.get(preLastIdx), 256)[0],
                divMod(ipv6Octets.get(preLastIdx), 256)[1],
                divMod(ipv6Octets.get(lastIdx), 256)[0],
                divMod(ipv6Octets.get(lastIdx), 256)[1]);
        return MacAddress.getByName(macAddressStr);
    }

    private static long[] divMod(long a, long b) {
        long result[] = new long[2];

        result[1] = a % b;
        result[0] = (a - result[1])/b;

        return result;
    }

    private static String expandIPv6Address(String shortAddress) {
        String[] addressArray = shortAddress.split(":");
        if (shortAddress.startsWith(":")) {
            addressArray[0] = "0";
        } else if(shortAddress.endsWith(":")) {
            addressArray[addressArray.length - 1] = "0";
        }

        for(int i = 0; i< addressArray.length; i++) {
            if (addressArray[i] == null || addressArray[i].isEmpty()) {
                StringBuilder sb = new StringBuilder();

                int leftSize = i +1;
                String[] left = new String[i+1];
                System.arraycopy(addressArray, 0, left, 0, leftSize);

                sb.append(Arrays.stream(left).collect(Collectors.joining(":")));

                String[] expanded = Stream.generate(() -> "0").limit(9-addressArray.length).toArray(String[]::new);
                sb.append(Arrays.stream(expanded).collect(Collectors.joining(":")));
                sb.append(":");

                int rightSize = addressArray.length - i - 1;
                String[] right = new String[rightSize];
                System.arraycopy(addressArray, i+1, right, 0, rightSize);
                sb.append(Arrays.stream(right).collect(Collectors.joining(":")));

                return sb.toString();
            }
        }
        return Arrays.stream(addressArray).collect(Collectors.joining(":"));
    }

    private static String generateIPv6AddrFromMAC(String mac) {
        String prefix = "fe80";
        String[] macOctets = mac.split(":");
        macOctets[0] = String.valueOf(Integer.parseInt(macOctets[0], 16) ^ 2);
        return String.format("%s::%s%s:%sff:fe%s:%s%s", prefix, macOctets[0], macOctets[1], macOctets[2], macOctets[3], macOctets[4], macOctets[5]);
    }
}
