package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Astf Global Info Per Template
 */
public class AstfGlobalInfoPerTemplate implements AstfGlobalInfoBase {
    private JsonObject tcp = new JsonObject();
    private JsonObject ip = new JsonObject();
    private static final Set<String> tcpParamSet = new HashSet(Arrays.asList(
            "initwnd", "mss", "no_delay", "rxbufsize", "txbufsize"));

    @Override
    public AstfGlobalInfoBase scheduler(SchedulerParam schedulerParam, int value) {
        throw new IllegalStateException("unsupported method in AstfGlobalInfoPerTemplate class");
    }

    @Override
    public AstfGlobalInfoBase ipv6(Ipv6Param ipv6Param, int value) {
        throw new IllegalStateException("unsupported method in AstfGlobalInfoPerTemplate class");
    }

    @Override
    public AstfGlobalInfoBase tcp(TcpParam tcpParam, int value) {
        if (!tcpParamSet.contains(tcpParam.getType())) {
            throw new IllegalStateException(String.format("TcpParam: %s is not support in AstfGlobalInfoPerTemplate class", tcpParam.getType()));
        }
        tcp.addProperty(tcpParam.getType(), value);
        return this;
    }

    @Override
    public AstfGlobalInfoBase ip(IpParam ipParam, int value) {
        ip.addProperty(ipParam.getType(), value);
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        if (tcp.size() != 0) {
            jsonObject.add("tcp", this.tcp);
        }
        if (ip.size() != 0) {
            jsonObject.add("ip", this.ip);
        }
        return jsonObject;
    }
}
