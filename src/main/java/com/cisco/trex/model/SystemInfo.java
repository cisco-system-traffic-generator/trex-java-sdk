package com.cisco.trex.model;

import java.util.List;

public class SystemInfo {
  private String core_type;
  private Integer dp_core_count;
  private Integer dp_core_count_per_port;
  private String hostname;
  private Integer port_count;
  private List<Port> ports;

  public SystemInfo(
      String core_type,
      Integer dp_core_count,
      Integer dp_core_count_per_port,
      String hostname,
      Integer port_count,
      List<Port> ports) {
    this.core_type = core_type;
    this.dp_core_count = dp_core_count;
    this.dp_core_count_per_port = dp_core_count_per_port;
    this.hostname = hostname;
    this.port_count = port_count;
    this.ports = ports;
  }

  public List<Port> getPorts() {
    return ports;
  }

  public String getCoreType() {
    return core_type;
  }

  public String getHostname() {
    return hostname;
  }

  public int getCoreCount() {
    return dp_core_count;
  }

  public int getCoreCountPerPort() {
    return dp_core_count_per_port;
  }

  public int getPortCount() {
    return port_count;
  }
}
