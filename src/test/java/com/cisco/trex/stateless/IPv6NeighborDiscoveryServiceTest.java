package com.cisco.trex.stateless;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.cisco.trex.model.port.PortVlan;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.MacAddress;

public class IPv6NeighborDiscoveryServiceTest {

  @Test
  public void generateIPv6AddrFromMACTest() {
    String ipV6Address = IPv6NeighborDiscoveryService.generateIPv6AddrFromMAC("10:62:E5:09:A0:64");
    assertEquals("fe80::1262:e5ff:fe09:a064", ipV6Address);
  }

  @Test
  public void generateMulticastMacFromIPv6Test() {
    MacAddress multicastMac =
        IPv6NeighborDiscoveryService.multicastMacFromIPv6("fe80::4a5d:60ff:fee8:8f");
    assertEquals("33:33:ff:e8:00:8f", multicastMac.toString());
    multicastMac =
        IPv6NeighborDiscoveryService.multicastMacFromIPv6(
            "2001:FF00:0099:0000:0000:0010:EEEE:0011");
    assertEquals("33:33:ff:ee:00:11", multicastMac.toString());
  }

  @Test
  public void buildICMPV6NSPktTest() {
    PortVlan vlan = new PortVlan();
    List<Integer> vlans = new ArrayList<>();
    vlans.add(10);
    vlan.setTags(vlans);
    String srcMac = "00:01:44:ff:01:01";
    String dstIp = "2001:FF00:0099:0000:0000:0010:EEEE:0011";
    String srcIp = "2001:FF00:0099:0000:0000:0010:EEEE:0010";
    final String dstMac = IPv6NeighborDiscoveryService.multicastMacFromIPv6(dstIp).toString();
    Packet pkt = IPv6NeighborDiscoveryService.buildICMPV6NSPkt(vlan, srcMac, dstMac, dstIp, srcIp);
    assertTrue(pkt.toString().contains("Checksum: 0x154a"));
  }
}
