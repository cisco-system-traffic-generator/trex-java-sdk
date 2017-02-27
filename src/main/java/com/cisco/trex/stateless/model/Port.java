package com.cisco.trex.stateless.model;

import java.util.List;

public class Port {
    public String description;
    public String driver;
    public String dst_macaddr;
    public String hw_macaddr;
    public Integer index;
    public Boolean is_fc_supported;
    public Boolean is_led_supported;
    public Boolean is_link_supported;
    public Boolean is_virtual;
    public String numa;
    public String pci_addr;
    public RxStats rx;
    public Integer speed;
    public String src_macaddr;
    public List<Integer> supp_speeds;

    public int getIndex() {
        return index;
    }

    class RxStats {
        public Integer counters;
        public List<PortCapability> caps;

        public RxStats(Integer counters, List<PortCapability> caps) {
            this.counters = counters;
            this.caps = caps;
        }
    }

    enum PortCapability {
        flow_stats("flow_stats"), latency("latency"), rx_bytes("rx_bytes");

        String capName;
        
        PortCapability(String capName) {
            this.capName = capName;
        }
    }
}
