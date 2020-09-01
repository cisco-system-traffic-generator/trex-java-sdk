package com.cisco.trex.stateful.api.lowlevel;

import org.apache.commons.lang3.StringUtils;

/** Java implementation for TRex python sdk ASTFCapInfo class */
public class ASTFCapInfo {

  // pcap file name. Filesystem directory location is relative to the profile file
  private String filePath;
  // rule for server association in default take the destination port from pcap file
  private ASTFAssociation assoc;
  private float cps; // new connection per second rate
  private ASTFIpGen astfIpGen; // tuple generator for this template
  private int port; // Override destination port, by default is taken from pcap
  private float l7Percent; // L7 stream bandwidth percent
  private ASTFGlobalInfoPerTemplate serverGlobInfo; // server global param
  private ASTFGlobalInfoPerTemplate clientGlobInfo; // client global param
  private int limit; // Limit the number of flows
  private String tgName; // template group name
  private int udpMtu; // MTU for udp packtes
  private boolean cont; // try to keep the number of flows up to limit
  private ASTFCmd sDelay =
      null; // ASTFCmdDelay or ASTFCmdDelayRnd . Server delay command before sending response back
  // to client. defaults to None means no delay.
  private static final String DEFAULT_TG_NAME = "DefaultTgName";

  ASTFCapInfo(AstfCapInfoBuilder builder) {
    this.filePath = builder.filePath;
    this.cps = builder.cps;
    this.assoc = builder.assoc;
    this.astfIpGen = builder.astfIpGen;
    this.port = builder.port;
    this.l7Percent = builder.l7Percent;
    this.serverGlobInfo = builder.serverGlobInfo;
    this.clientGlobInfo = builder.clientGlobInfo;
    this.limit = builder.limit;
    this.cont = builder.cont;
    this.tgName = builder.tgName;
    this.sDelay = builder.sDelay;
    this.udpMtu = builder.udpMtu;
    paramCheck();
  }

  /**
   * new AstfCapInfo builder
   *
   * @return new builder
   */
  public static AstfCapInfoBuilder newBuilder() {
    return new AstfCapInfoBuilder();
  }

  private void paramCheck() {
    if (l7Percent > 0) {
      if (cps > 0) {
        throw new IllegalStateException(
            String.format("bad param combination,l7Percent %s ,cps %s ", l7Percent, cps));
      }
    } else {
      if (cps <= 0) {
        cps = 1;
      }
    }

    if (assoc == null) {
      if (port > 0) {
        assoc = new ASTFAssociation(new ASTFAssociationRule(port));
      } else {
        assoc = null;
      }
    } else {
      if (port > 0) {
        throw new IllegalStateException(
            String.format("bad param combination,assoc %s ,port %s ", assoc, port));
      }
    }

    if (StringUtils.isEmpty(tgName)) {
      tgName = DEFAULT_TG_NAME;
    } else {
      if (tgName.length() > 20) {
        throw new IllegalStateException("tgName is longer than 20");
      }
    }
  }

  /**
   * getFilePath
   *
   * @return filePath
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * getCps
   *
   * @return cps
   */
  public float getCps() {
    return cps;
  }

  /**
   * getAssoc
   *
   * @return assoc
   */
  public ASTFAssociation getAssoc() {
    return assoc;
  }

  /**
   * getAstfIpGen
   *
   * @return astfIpGen
   */
  public ASTFIpGen getAstfIpGen() {
    return astfIpGen;
  }

  /**
   * getPort
   *
   * @return port
   */
  public int getPort() {
    return port;
  }

  /**
   * getL7Percent
   *
   * @return l7Percent
   */
  public float getL7Percent() {
    return l7Percent;
  }

  /**
   * getServerGlobInfo
   *
   * @return serverGlobInfo
   */
  public ASTFGlobalInfoPerTemplate getServerGlobInfo() {
    return serverGlobInfo;
  }

  /**
   * getClientGlobInfo
   *
   * @return clientGlobInfo
   */
  public ASTFGlobalInfoPerTemplate getClientGlobInfo() {
    return clientGlobInfo;
  }

  /**
   * getLimit
   *
   * @return limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * get TgName
   *
   * @return tgName
   */
  public String getTgName() {
    return tgName;
  }

  /**
   * get UdpMtu
   *
   * @return udpMtu
   */
  public int getUdpMtu() {
    return udpMtu;
  }

  /**
   * isCont
   *
   * @return cont
   */
  public boolean isCont() {
    return cont;
  }

  /**
   * getsDelay
   *
   * @return sDelay
   */
  public ASTFCmd getsDelay() {
    return sDelay;
  }

  /** AstfCapInfo builder */
  public static final class AstfCapInfoBuilder {

    String filePath;
    float cps;
    ASTFAssociation assoc;
    ASTFIpGen astfIpGen;
    int port;
    float l7Percent;
    ASTFGlobalInfoPerTemplate serverGlobInfo;
    ASTFGlobalInfoPerTemplate clientGlobInfo;
    int limit;
    boolean cont;
    String tgName;
    int udpMtu;
    ASTFCmd sDelay;

    public AstfCapInfoBuilder filePath(String filePath) {
      this.filePath = filePath;
      return this;
    }

    public AstfCapInfoBuilder cps(float cps) {
      this.cps = cps;
      return this;
    }

    public AstfCapInfoBuilder assoc(ASTFAssociation assoc) {
      this.assoc = assoc;
      return this;
    }

    public AstfCapInfoBuilder astfIpGen(ASTFIpGen astfIpGen) {
      this.astfIpGen = astfIpGen;
      return this;
    }

    public AstfCapInfoBuilder port(int port) {
      this.port = port;
      return this;
    }

    public AstfCapInfoBuilder l7Percent(float l7Percent) {
      this.l7Percent = l7Percent;
      return this;
    }

    public AstfCapInfoBuilder serverGlobInfo(ASTFGlobalInfoPerTemplate serverGlobInfo) {
      this.serverGlobInfo = serverGlobInfo;
      return this;
    }

    public AstfCapInfoBuilder clientGlobInfo(ASTFGlobalInfoPerTemplate clientGlobInfo) {
      this.clientGlobInfo = clientGlobInfo;
      return this;
    }

    public AstfCapInfoBuilder limit(int limit) {
      this.limit = limit;
      return this;
    }

    public AstfCapInfoBuilder cont(boolean cont) {
      this.cont = cont;
      return this;
    }

    public AstfCapInfoBuilder tgName(String tgName) {
      this.tgName = tgName;
      return this;
    }

    public AstfCapInfoBuilder udpMtu(int udpMtu) {
      this.udpMtu = udpMtu;
      return this;
    }

    public AstfCapInfoBuilder sDelay(ASTFCmd sDelay) {
      this.sDelay = sDelay;
      return this;
    }

    public ASTFCapInfo build() {
      return new ASTFCapInfo(this);
    }
  }
}
