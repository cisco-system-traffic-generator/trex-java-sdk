package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * ASTFGlobalInfoBase interface Extract the same params(scheduler,ipv6,ip,tcp) code in class
 * ASTFGlobalInfo & ASTFGlobalInfoPerTemplate to this class. For the different type about
 * params,abstract their create object method in this class, so the class ASTFGlobalInfo &
 * ASTFGlobalInfoPerTemplate can reduce the same code-block.
 */
public interface ASTFGlobalInfoBase {

  /**
   * scheduler global info
   *
   * @param schedulerParam
   * @param value
   * @return this
   */
  ASTFGlobalInfoBase scheduler(SchedulerParam schedulerParam, int value);

  /**
   * ipv6 global info
   *
   * @param ipv6Param
   * @param value
   * @return this
   */
  ASTFGlobalInfoBase ipv6(Ipv6Param ipv6Param, int value);

  /**
   * tcp global info
   *
   * @param tcpParam
   * @param value
   * @return this
   */
  ASTFGlobalInfoBase tcp(TcpParam tcpParam, int value);

  /**
   * ip global info
   *
   * @param ipParam
   * @param value
   * @return this
   */
  ASTFGlobalInfoBase ip(IpParam ipParam, int value);

  /**
   * to json format
   *
   * @return json string
   */
  JsonObject toJson();

  /** Scheduler Param enum */
  enum SchedulerParam {
    RAMPUP_SEC("rampup_sec"),
    ACCURATE("accurate");
    String type;

    /**
     * Construct
     *
     * @param type
     */
    SchedulerParam(String type) {
      this.type = type;
    }

    /**
     * get scheduler type
     *
     * @return type
     */
    public String getType() {
      return type;
    }
  }

  /** Ipv6 Param enum */
  enum Ipv6Param {
    SRC_MSB("src_msb"),
    DST_MSB("dst_msb"),
    ENABLE("enable");

    String type;

    /**
     * Construct
     *
     * @param type
     */
    Ipv6Param(String type) {
      this.type = type;
    }

    /**
     * get ipv6 type
     *
     * @return type
     */
    public String getType() {
      return type;
    }
  }

  /** Tcp Param enum */
  enum TcpParam {
    MSS("mss"),
    INIT_WND("initwnd"),
    RX_BUF_SIZE("rxbufsize"),
    TX_BUF_SIZE("txbufsize"),
    REXMTTHRESH("rexmtthresh"),
    DO_RFC1323("do_rfc1323"),
    DO_SACK("do_sack"),
    CC_ALGO("cc_algo"),
    REASS_MAX_QLEN("reass_maxqlen"),
    KEEP_INIT("keepinit"),
    KEEP_IDLE("keepidle"),
    KEEP_INTVL("keepintvl"),
    BLACK_HOLE("blackhole"),
    DELAY_ACK_MSEC("delay_ack_msec"),
    NO_DELAY("no_delay"),
    NO_DELAY_COUNTER("no_delay_counter");
    String type;

    /**
     * Construct
     *
     * @param type
     */
    TcpParam(String type) {
      this.type = type;
    }

    /**
     * get tcp type
     *
     * @return type
     */
    public String getType() {
      return type;
    }
  }

  /** Ip Param enum */
  enum IpParam {
    TOS("tos"),
    TTL("ttl"),
    DONT_USE_INBOUND_MAC("dont_use_inbound_mac");

    String type;

    /**
     * Construct
     *
     * @param type
     */
    IpParam(String type) {
      this.type = type;
    }

    /**
     * get ip type
     *
     * @return type
     */
    public String getType() {
      return type;
    }
  }
}
