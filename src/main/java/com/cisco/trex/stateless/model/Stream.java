package com.cisco.trex.stateless.model;

import org.pcap4j.packet.Packet;
import java.util.Base64;
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
    private StreamPacket packet;
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
        String pkt = Base64.getEncoder().encodeToString(packet.getRawData());
        this.packet = new StreamPacket(pkt);
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

    public StreamPacket getPacket() {
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

    public static class StreamPacket {
        /**
         * Binary representation encoded as base64 string
         */
        private String binary;
        private String meta = "";
    
        public StreamPacket(String binaryBase64) {
            this(binaryBase64, "");
        }
    
        public StreamPacket(String binaryBase64, String meta) {
            binary = binaryBase64;
            this.meta = meta;
        }
    
    
        public String getBinary() {
            return binary;
        }
    
        public byte[] getBytes() {
            return Base64.getDecoder().decode(binary.getBytes());
        }
    
        public String getMeta() {
            return meta;
        }
    }
}
