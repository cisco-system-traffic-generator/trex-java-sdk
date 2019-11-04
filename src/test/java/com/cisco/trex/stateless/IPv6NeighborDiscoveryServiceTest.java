package com.cisco.trex.stateless;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
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
        IPv6NeighborDiscoveryService.multicastMacFromIPv6("fe80::4a5d:60ff:fee8:658f");
    assertEquals("33:33:ff:e8:65:8f", multicastMac.toString());
  }
}
