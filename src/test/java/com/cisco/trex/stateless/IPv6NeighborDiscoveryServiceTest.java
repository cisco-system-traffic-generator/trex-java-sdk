package com.cisco.trex.stateless;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IPv6NeighborDiscoveryServiceTest {

  @Test
  public void generateIPv6AddrFromMACTest() {
    String ipV6Address = IPv6NeighborDiscoveryService.generateIPv6AddrFromMAC("10:62:E5:09:A0:64");
    assertEquals("fe80::1262:e5ff:fe09:a064", ipV6Address);
  }
}
