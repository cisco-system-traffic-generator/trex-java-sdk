package com.cisco.trex.stateful.model.stats;

import com.google.gson.annotations.SerializedName;

public class LatencyPortStats {

    @SerializedName("ignore_bytes")
    public Integer ignoreBytes;

    @SerializedName("ipv6_n_solic")
    public Integer ipv6NSolic;

    @SerializedName("m_jitter")
    public Integer mJitter;

    @SerializedName("m_l3_cs_err")
    public Integer mL3CsErr;

    @SerializedName("m_l4_cs_err")
    public Integer mL4CsErr;

    @SerializedName("m_length_error")
    public Integer mLengthError;

    @SerializedName("m_no_id")
    public Integer mNoId;

    @SerializedName("m_no_ipv4_option")
    public Integer mNoIpv4Option;

    @SerializedName("m_no_magic")
    public Integer mNoMagic;

    @SerializedName("m_pkt_ok")
    public Integer mPktOk;

    @SerializedName("m_rx_check")
    public Integer mRxCheck;

    @SerializedName("m_seq_error")
    public Integer mSeqError;

    @SerializedName("m_tx_pkt_err")
    public Integer mTxPktErr;

    @SerializedName("m_tx_pkt_ok")
    public Integer mTxPktOk;

    @SerializedName("m_unsup_prot")
    public Integer mUnsupProt;

    @SerializedName("tx_arp")
    public Integer txArp;

    @SerializedName("ignore_bytes")
    public Integer getIgnoreBytes() {
        return ignoreBytes;
    }

    @SerializedName("ignore_bytes")
    public void setIgnoreBytes(Integer ignoreBytes) {
        this.ignoreBytes = ignoreBytes;
    }

    @SerializedName("ipv6_n_solic")
    public Integer getIpv6NSolic() {
        return ipv6NSolic;
    }

    @SerializedName("ipv6_n_solic")
    public void setIpv6NSolic(Integer ipv6NSolic) {
        this.ipv6NSolic = ipv6NSolic;
    }

    @SerializedName("m_jitter")
    public Integer getmJitter() {
        return mJitter;
    }

    @SerializedName("m_jitter")
    public void setmJitter(Integer mJitter) {
        this.mJitter = mJitter;
    }

    @SerializedName("m_l3_cs_err")
    public Integer getmL3CsErr() {
        return mL3CsErr;
    }

    @SerializedName("m_l3_cs_err")
    public void setmL3CsErr(Integer mL3CsErr) {
        this.mL3CsErr = mL3CsErr;
    }

    @SerializedName("m_l4_cs_err")
    public Integer getmL4CsErr() {
        return mL4CsErr;
    }

    @SerializedName("m_l4_cs_err")
    public void setmL4CsErr(Integer mL4CsErr) {
        this.mL4CsErr = mL4CsErr;
    }

    @SerializedName("m_length_error")
    public Integer getmLengthError() {
        return mLengthError;
    }

    @SerializedName("m_length_error")
    public void setmLengthError(Integer mLengthError) {
        this.mLengthError = mLengthError;
    }

    @SerializedName("m_no_id")
    public Integer getmNoId() {
        return mNoId;
    }

    @SerializedName("m_no_id")
    public void setmNoId(Integer mNoId) {
        this.mNoId = mNoId;
    }

    @SerializedName("m_no_ipv4_option")
    public Integer getmNoIpv4Option() {
        return mNoIpv4Option;
    }

    @SerializedName("m_no_ipv4_option")
    public void setmNoIpv4Option(Integer mNoIpv4Option) {
        this.mNoIpv4Option = mNoIpv4Option;
    }

    @SerializedName("m_no_magic")
    public Integer getmNoMagic() {
        return mNoMagic;
    }

    @SerializedName("m_no_magic")
    public void setmNoMagic(Integer mNoMagic) {
        this.mNoMagic = mNoMagic;
    }

    @SerializedName("m_pkt_ok")
    public Integer getmPktOk() {
        return mPktOk;
    }

    @SerializedName("m_pkt_ok")
    public void setmPktOk(Integer mPktOk) {
        this.mPktOk = mPktOk;
    }

    @SerializedName("m_rx_check")
    public Integer getmRxCheck() {
        return mRxCheck;
    }

    @SerializedName("m_rx_check")
    public void setmRxCheck(Integer mRxCheck) {
        this.mRxCheck = mRxCheck;
    }

    @SerializedName("m_seq_error")
    public Integer getmSeqError() {
        return mSeqError;
    }

    @SerializedName("m_seq_error")
    public void setmSeqError(Integer mSeqError) {
        this.mSeqError = mSeqError;
    }

    @SerializedName("m_tx_pkt_err")
    public Integer getmTxPktErr() {
        return mTxPktErr;
    }

    @SerializedName("m_tx_pkt_err")
    public void setmTxPktErr(Integer mTxPktErr) {
        this.mTxPktErr = mTxPktErr;
    }

    @SerializedName("m_tx_pkt_ok")
    public Integer getmTxPktOk() {
        return mTxPktOk;
    }

    @SerializedName("m_tx_pkt_ok")
    public void setmTxPktOk(Integer mTxPktOk) {
        this.mTxPktOk = mTxPktOk;
    }

    @SerializedName("m_unsup_prot")
    public Integer getmUnsupProt() {
        return mUnsupProt;
    }

    @SerializedName("m_unsup_prot")
    public void setmUnsupProt(Integer mUnsupProt) {
        this.mUnsupProt = mUnsupProt;
    }

    @SerializedName("tx_arp")
    public Integer getTxArp() {
        return txArp;
    }

    @SerializedName("tx_arp")
    public void setTxArp(Integer txArp) {
        this.txArp = txArp;
    }
}
