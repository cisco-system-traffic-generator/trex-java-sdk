package com.cisco.trex.stateful.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class LatencyStats {

    /**
     * For the structure of this data object:
     * <pre>
     * {
     *   "cpu_util": 0.0,
     *   "epoch": 24,
     *   "port-n": {
     *     "hist": {
     *       "cnt": 0,
     * 	     "high_cnt": 0,
     * 	     "histogram": [],
     * 	     "max_usec": 0,
     * 	     "min_usec": 10,
     * 	     "s_avg": 0.0,
     * 	     "s_max": 0.0
     *     },
     * 	   "stats": {
     *       "ignore_bytes": 0,
     *       "ipv6_n_solic": 0,
     *       "m_jitter": 0,
     *       "m_l3_cs_err": 0,
     *       "m_l4_cs_err": 0,
     *       "m_length_error": 0,
     *       "m_no_id": 0,
     *       "m_no_ipv4_option": 0,
     *       "m_no_magic": 0,
     *       "m_pkt_ok": 0,
     *       "m_rx_check": 0,
     *       "m_seq_error": 0,
     *       "m_tx_pkt_err": 0,
     *       "m_tx_pkt_ok": 11,
     *       "m_unsup_prot": 0,
     *       "tx_arp": 0
     *     }
     *   } ...
     * }
     * </pre>
     * see http://trex-tgn.cisco.com/trex/doc/cp_docs/api/json_fields.html#trex-latecny-field for detail description.
     */
    @JsonProperty("data")
    public Map data;

    @JsonProperty("name")
    public String name;

    @JsonProperty("type")
    public Integer type;

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
