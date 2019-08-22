package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/** Java implementation for TRex python sdk ASTFGlobalInfo class */
public class ASTFGlobalInfo implements ASTFGlobalInfoBase {
  private JsonObject scheduler = new JsonObject();
  private JsonObject ipv6 = new JsonObject();
  private JsonObject tcp = new JsonObject();
  private JsonObject ip = new JsonObject();

  @Override
  public ASTFGlobalInfo scheduler(SchedulerParam schedulerParam, int value) {
    scheduler.addProperty(schedulerParam.getType(), value);
    return this;
  }

  @Override
  public ASTFGlobalInfo ipv6(Ipv6Param ipv6Param, int value) {
    ipv6.addProperty(ipv6Param.getType(), value);
    return this;
  }

  @Override
  public ASTFGlobalInfo tcp(TcpParam tcpParam, int value) {
    tcp.addProperty(tcpParam.getType(), value);
    return this;
  }

  @Override
  public ASTFGlobalInfo ip(IpParam ipParam, int value) {
    ip.addProperty(ipParam.getType(), value);
    return this;
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (scheduler.size() != 0) {
      json.add("scheduler", this.scheduler);
    }
    if (ipv6.size() != 0) {
      json.add("ipv6", this.ipv6);
    }
    if (tcp.size() != 0) {
      json.add("tcp", this.tcp);
    }
    if (ip.size() != 0) {
      json.add("ip", this.ip);
    }
    return json;
  }
}
