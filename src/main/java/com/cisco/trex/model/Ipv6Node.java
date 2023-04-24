package com.cisco.trex.model;

public class Ipv6Node {
  private String mac;
  private String ip;
  private boolean isRouter;

  public Ipv6Node(String mac, String ip, boolean isRouter) {
    this.mac = mac;
    this.ip = ip;
    this.isRouter = isRouter;
  }

  public String getMac() {
    return mac;
  }

  public String getIp() {
    return ip;
  }

  public boolean isRouter() {
    return isRouter;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Ipv6Node)) {
      return false;
    }
    return this.ip.equalsIgnoreCase(((Ipv6Node) o).ip);
  }

  // Idea from effective Java : Item 9
  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + ip.hashCode();
    return result;
  }
}
