package com.cisco.trex.stateless.model;

import java.util.HashMap;
import java.util.Map;

public class Stream {
    private Integer id;
    private Integer flags = 1;
    private Integer action_count = 0;
    private Integer random_seed = 0;
    private Boolean enabled;
    private Double isg;
    private StreamMode mode;
    private Integer next_stream_id;
    private Packet packet;
    private StreamRxStats rx_stats;
    private StreamVM vm;
    private Boolean self_start;
    private Map<String, Object> flow_stats = new HashMap<>();

        public Stream(Integer id, Boolean enabled, Double isg, StreamMode mode, Integer next_stream_id, Packet packet, StreamRxStats rx_stats, StreamVM vm, Boolean self_start) {
        this.id = id;
        this.enabled = enabled;
        this.isg = isg;
        this.mode = mode;
        this.next_stream_id = next_stream_id;
        this.packet = packet;
        this.rx_stats = rx_stats;
        this.vm = vm;
        this.self_start = self_start;
        flow_stats.put("enabled", false);
        flow_stats.put("stream_id", id);
    }

    public Integer getId() {
        return id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Double getIsg() {
        return isg;
    }

    public StreamMode getMode() {
        return mode;
    }

    public Integer getNext_stream_id() {
        return next_stream_id;
    }

    public Packet getPacket() {
        return packet;
    }

    public StreamRxStats getRx_stats() {
        return rx_stats;
    }

    public StreamVM getVm() {
        return vm;
    }

    public Boolean getSelf_start() {
        return self_start;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Stream)) {
            return false;
        }
        
        Stream s2 = (Stream) obj;
        return id.equals(s2.getId()) || super.equals(obj);
    }
}
