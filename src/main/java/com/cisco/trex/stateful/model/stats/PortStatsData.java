package com.cisco.trex.stateful.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PortStatsData {

    @JsonProperty("ignore_bytes")
    public Integer ignore_bytes;

    @JsonProperty("ipv6_n_solic")
    public Integer ipv6_n_solic;

    @JsonProperty("m_jitter")
    public Integer m_jitter;

    @JsonProperty("m_l3_cs_err")
    public Integer m_l3_cs_err;

    @JsonProperty("m_l4_cs_err")
    public Integer m_l4_cs_err;

    @JsonProperty("m_length_error")
    public Integer m_length_error;

    @JsonProperty("m_no_id")
    public Integer m_no_id;

    @JsonProperty("m_no_ipv4_option")
    public Integer m_no_ipv4_option;

    @JsonProperty("m_no_magic")
    public Integer m_no_magic;

    @JsonProperty("m_pkt_ok")
    public Integer m_pkt_ok;

    @JsonProperty("m_rx_check")
    public Integer m_rx_check;

    @JsonProperty("m_seq_error")
    public Integer m_seq_error;

    @JsonProperty("m_tx_pkt_err")
    public Integer m_tx_pkt_err;

    @JsonProperty("m_tx_pkt_ok")
    public Integer m_tx_pkt_ok;

    @JsonProperty("m_unsup_prot")
    public Integer m_unsup_prot;

    @JsonProperty("tx_arp")
    public Integer tx_arp;

    public Integer getIgnore_bytes() {
        return ignore_bytes;
    }

    public void setIgnore_bytes(Integer ignore_bytes) {
        this.ignore_bytes = ignore_bytes;
    }

    public Integer getIpv6_n_solic() {
        return ipv6_n_solic;
    }

    public void setIpv6_n_solic(Integer ipv6_n_solic) {
        this.ipv6_n_solic = ipv6_n_solic;
    }

    public Integer getM_jitter() {
        return m_jitter;
    }

    public void setM_jitter(Integer m_jitter) {
        this.m_jitter = m_jitter;
    }

    public Integer getM_l3_cs_err() {
        return m_l3_cs_err;
    }

    public void setM_l3_cs_err(Integer m_l3_cs_err) {
        this.m_l3_cs_err = m_l3_cs_err;
    }

    public Integer getM_l4_cs_err() {
        return m_l4_cs_err;
    }

    public void setM_l4_cs_err(Integer m_l4_cs_err) {
        this.m_l4_cs_err = m_l4_cs_err;
    }

    public Integer getM_length_error() {
        return m_length_error;
    }

    public void setM_length_error(Integer m_length_error) {
        this.m_length_error = m_length_error;
    }

    public Integer getM_no_id() {
        return m_no_id;
    }

    public void setM_no_id(Integer m_no_id) {
        this.m_no_id = m_no_id;
    }

    public Integer getM_no_ipv4_option() {
        return m_no_ipv4_option;
    }

    public void setM_no_ipv4_option(Integer m_no_ipv4_option) {
        this.m_no_ipv4_option = m_no_ipv4_option;
    }

    public Integer getM_no_magic() {
        return m_no_magic;
    }

    public void setM_no_magic(Integer m_no_magic) {
        this.m_no_magic = m_no_magic;
    }

    public Integer getM_pkt_ok() {
        return m_pkt_ok;
    }

    public void setM_pkt_ok(Integer m_pkt_ok) {
        this.m_pkt_ok = m_pkt_ok;
    }

    public Integer getM_rx_check() {
        return m_rx_check;
    }

    public void setM_rx_check(Integer m_rx_check) {
        this.m_rx_check = m_rx_check;
    }

    public Integer getM_seq_error() {
        return m_seq_error;
    }

    public void setM_seq_error(Integer m_seq_error) {
        this.m_seq_error = m_seq_error;
    }

    public Integer getM_tx_pkt_err() {
        return m_tx_pkt_err;
    }

    public void setM_tx_pkt_err(Integer m_tx_pkt_err) {
        this.m_tx_pkt_err = m_tx_pkt_err;
    }

    public Integer getM_tx_pkt_ok() {
        return m_tx_pkt_ok;
    }

    public void setM_tx_pkt_ok(Integer m_tx_pkt_ok) {
        this.m_tx_pkt_ok = m_tx_pkt_ok;
    }

    public Integer getM_unsup_prot() {
        return m_unsup_prot;
    }

    public void setM_unsup_prot(Integer m_unsup_prot) {
        this.m_unsup_prot = m_unsup_prot;
    }

    public Integer getTx_arp() {
        return tx_arp;
    }

    public void setTx_arp(Integer tx_arp) {
        this.tx_arp = tx_arp;
    }
}
