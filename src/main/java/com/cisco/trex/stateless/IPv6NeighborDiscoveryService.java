package com.cisco.trex.stateless;

import com.cisco.trex.stateless.exception.ServiceModeRequiredException;
import com.cisco.trex.stateless.model.Ipv6Node;
import com.cisco.trex.stateless.model.PortStatus;
import com.cisco.trex.stateless.model.TRexClientResult;
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

import static org.pcap4j.util.ByteArrays.BYTE_SIZE_IN_BYTES;

public class IPv6NeighborDiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPv6NeighborDiscoveryService.class);

    private TRexClient tRexClient;
    private String srcMac;
    private int portIdx;
    private Map<String, Ipv6Node> ipv6Nodes = new HashMap<>();
    private long endTs;

    public IPv6NeighborDiscoveryService(TRexClient tRexClient) {
        this.tRexClient = tRexClient;
    }
    
    public Map<String, Ipv6Node> scan(int portIdx, int timeDuration)  throws ServiceModeRequiredException {
        ipv6Nodes.clear();
        endTs = System.currentTimeMillis() + timeDuration * 1000;
        TRexClientResult<PortStatus> portStatusResult = tRexClient.getPortStatus(portIdx);
        PortStatus portStatus = portStatusResult.get();

        if (!portStatus.getService()) {
            throw new ServiceModeRequiredException();
        }

        srcMac = portStatus.getAttr().getLayerConiguration().getL2Configuration().getSrc();
        this.portIdx = portIdx;

        Packet icmpv6EchoReqPkt = buildICMPV6EchoReq(srcMac);
        sendIcmpv6Pkt(icmpv6EchoReqPkt);
        tRexClient.removeRxQueue(portIdx);
        
        return ipv6Nodes;

    }

    private void sendPkt(List<Packet> pkts) {
        if (endTs < System.currentTimeMillis()) {
            return;
        }
        tRexClient.removeRxQueue(portIdx);
        tRexClient.setRxQueue(portIdx, 1000);
        if (pkts.size() == 1) {
            tRexClient.sendPacket(portIdx, pkts.get(0));
        } else {
            tRexClient.sendPackets(portIdx, pkts);
        }

        Predicate<EthernetPacket> ipV6NDPktFilter = etherPkt -> etherPkt.contains(IcmpV6NeighborSolicitationPacket.class) || etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class);

        tRexClient.getRxQueue(portIdx, ipV6NDPktFilter).forEach(this::onPktReceived);
    }

    private void sendIcmpv6Pkt(Packet icmpv6EchoReqPkt) {
        tRexClient.removeRxQueue(portIdx);
        tRexClient.setRxQueue(portIdx, 1000);
        tRexClient.sendPacket(portIdx, icmpv6EchoReqPkt);
        Predicate<EthernetPacket> ipV6NDPktFilter = etherPkt -> etherPkt.contains(IcmpV6NeighborSolicitationPacket.class) || etherPkt.contains(IcmpV6NeighborAdvertisementPacket.class) || etherPkt.contains(IcmpV6EchoReplyPacket.class);
        int tryCount = 10;
        List<Packet> rxQueue = new ArrayList<>();
        while(tryCount > 0) {
            tryCount--;
            rxQueue.addAll(tRexClient.getRxQueue(portIdx, ipV6NDPktFilter));
            if (rxQueue.size() > 0) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        rxQueue.forEach(this::onPktReceived);
    }

    private void onPktReceived(Packet pkt) {
        if (pkt.contains(IcmpV6NeighborSolicitationPacket.class)) {
            IpV6Packet ipV6Packet = pkt.get(IpV6Packet.class);
            String nodeIp = ipV6Packet.getHeader().getSrcAddr().toString().substring(1);
            
            String nodeMac = getLinkLayerAddress(ipV6Packet);

            Packet icmpv6NSPkt = buildICMPV6NSPkt(nodeMac, nodeIp);
            Packet icmpv6NAPkt = buildICMPV6NAPkt(nodeMac, nodeIp);
            sendPkt(Arrays.asList(icmpv6NSPkt, icmpv6NAPkt));
        } else if (pkt.contains(IcmpV6NeighborAdvertisementPacket.class)) {
            IcmpV6NeighborAdvertisementHeader icmpV6NaHdr = pkt.get(IcmpV6NeighborAdvertisementPacket.class).getHeader();
            
            boolean is_router = icmpV6NaHdr.getRouterFlag();
            
            String nodeIp = icmpV6NaHdr.getTargetAddress().toString().substring(1);

            IpV6Packet.IpV6Header ipV6Header = pkt.get(IpV6Packet.class).getHeader();
            String dstIp = ipV6Header.getDstAddr().toString().substring(1).replace("fe80:0:0:0:", "fe80::");
            String nodeMac = ((EthernetPacket) pkt).getHeader().getSrcAddr().toString();
            
            if(!ipv6Nodes.containsKey(nodeIp)) {
                ipv6Nodes.put(nodeIp, new Ipv6Node(nodeMac, nodeIp, is_router));
            }
        }
    }

    private Packet buildICMPV6NSPkt(String dstMac, String dstIp) {
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
            

            IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
            icmpCommonPktBuilder
                    .srcAddr((Inet6Address) Inet6Address.getByName(generateIPv6AddrFromMAC(srcMac)))
                    .dstAddr((Inet6Address) Inet6Address.getByName(dstIp))
                    .type(IcmpV6Type.NEIGHBOR_SOLICITATION)
                    .code(IcmpV6Code.NO_CODE)
                    .correctChecksumAtBuild(true)
                    .payloadBuilder(ipv6NSBuilder);

            IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
            ipV6Builder
                    .srcAddr((Inet6Address) Inet6Address.getByName(generateIPv6AddrFromMAC(srcMac)))
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

    private Packet buildICMPV6NAPkt(String dstMac, String dstIp) {
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
                         .targetAddress((Inet6Address) Inet6Address.getByName(generateIPv6AddrFromMAC(srcMac)));

            IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
            icmpCommonPktBuilder
                    .srcAddr((Inet6Address) Inet6Address.getByName(generateIPv6AddrFromMAC(srcMac)))
                    .dstAddr((Inet6Address) Inet6Address.getByName(dstIp))
                    .type(IcmpV6Type.NEIGHBOR_ADVERTISEMENT)
                    .code(IcmpV6Code.NO_CODE)
                    .correctChecksumAtBuild(true)
                    .payloadBuilder(ipv6NABuilder);

            IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
            ipV6Builder
                    .srcAddr((Inet6Address) Inet6Address.getByName(generateIPv6AddrFromMAC(srcMac)))
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
    
    private EthernetPacket buildICMPV6EchoReq(String srcMac) {

        IcmpV6EchoRequestPacket.Builder icmpV6ERBuilder = new IcmpV6EchoRequestPacket.Builder();
        icmpV6ERBuilder
                .identifier((short) 0)
                .sequenceNumber((short) 0);

        IcmpV6CommonPacket.Builder icmpCommonPktBuilder = new IcmpV6CommonPacket.Builder();
        try {
            icmpCommonPktBuilder
                    .srcAddr((Inet6Address) Inet6Address.getByName(generateIPv6AddrFromMAC(srcMac)))
                    .dstAddr((Inet6Address) Inet6Address.getByName("ff02:0:0:0:0:0:0:1"))
                    .type(IcmpV6Type.ECHO_REQUEST)
                    .code(IcmpV6Code.NO_CODE)
                    .correctChecksumAtBuild(true)
                    .payloadBuilder(icmpV6ERBuilder);
            IpV6Packet.Builder ipV6Builder = new IpV6Packet.Builder();
            ipV6Builder
                    .srcAddr((Inet6Address) Inet6Address.getByName(generateIPv6AddrFromMAC(srcMac)))
                    .dstAddr((Inet6Address) Inet6Address.getByName("ff02:0:0:0:0:0:0:1"))
                    .version(IpVersion.IPV6)
                    .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0))
                    .flowLabel(IpV6SimpleFlowLabel.newInstance(0))
                    .nextHeader(IpNumber.ICMPV6)
                    .hopLimit((byte) 1)
                    .payloadBuilder(icmpCommonPktBuilder)
                    .correctLengthAtBuild(true);

            EthernetPacket.Builder ethBuilder = new EthernetPacket.Builder();
            ethBuilder
                    .type(EtherType.IPV6)
                    .srcAddr(MacAddress.getByName(srcMac))
                    .dstAddr(MacAddress.getByName("33:33:00:00:00:01"))
                    .payloadBuilder(ipV6Builder)
                    .paddingAtBuild(true);

            return ethBuilder.build();
        } catch (UnknownHostException ignore) {}
        return null;
    }

    private String generateIPv6AddrFromMAC(String mac) {
        String prefix = "fe80";
        String[] macOctets = mac.split(":");
        macOctets[0] = String.valueOf(Integer.parseInt(macOctets[0], 16) ^ 2);
        return String.format("%s::%s%s:%sff:fe%s:%s%s", prefix, macOctets[0],macOctets[1],macOctets[2],macOctets[3],macOctets[4],macOctets[5]);
    }
}
