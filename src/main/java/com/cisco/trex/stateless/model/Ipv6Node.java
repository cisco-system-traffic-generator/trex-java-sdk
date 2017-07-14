package com.cisco.trex.stateless.model;

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
}
