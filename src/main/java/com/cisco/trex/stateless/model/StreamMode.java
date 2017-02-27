package com.cisco.trex.stateless.model;

public class StreamMode {

    /**
     * Total packets in the burst
     */
    private Integer total_pkts;

    /**
     * Packets in a single burst
     */
    private Integer pkts_per_burst;

    /**
     * Number of bursts. '0' means loop forever, '1' will fall back to single burst
     */
    private Integer count;

    /**
     * [usec] inter burst gap. delay between bursts in usec
     */
    private Double ibg;
    
    private StreamModeRate rate;
    
    private Type type;

    public StreamMode(Integer total_pkts, Integer pkts_per_burst, Integer count, Double ibg, StreamModeRate rate, Type type) {
        this.total_pkts = total_pkts;
        this.pkts_per_burst = pkts_per_burst;
        this.count = count;
        this.ibg = ibg;
        this.rate = rate;
        this.type = type;
    }

    public Integer getTotal_pkts() {
        return total_pkts;
    }

    public Integer getPkts_per_burst() {
        return pkts_per_burst;
    }

    public Integer getCount() {
        return count;
    }

    public Double getIbg() {
        return ibg;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        single_burst("single_burst"), multi_burst("multi_burst"), continuous("continuous");
        
        String type;

        Type(String type) {
            this.type = type;
        }
    }
}
