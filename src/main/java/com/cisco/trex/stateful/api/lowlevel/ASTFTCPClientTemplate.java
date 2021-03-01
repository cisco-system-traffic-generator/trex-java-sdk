package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk ASTFTCPClientTemplate class
 *
 * <p>One manual template
 *
 * <p>client commands <code> ASTFProgram progC =new ASTFProgram()
 * progC.send(http_req)
 * progC.recv(http_response.length())
 * </code>
 *
 * <p>ip generator <code>
 *  ASTFIpGenDist ipGenC =new ASTFIpGenDist("16.0.0.0", "16.0.0.255",Distribution.SEQ);
 * ASTFIpGenDist ipGenS =new ASTFIpGenDist("48.0.0.0", "48.0.255.255",Distribution.SEQ);
 * ASTFIpGen ipGen = new ASTFIpGen(ipGenC,ipGenS,new ASTFIpGenGlobal("1.0.0.0");
 * </code>
 *
 * <p>template <code> ASTFTCPClientTemplate tempC=new ASTFTCPClientTemplate(progC, ipGen)</code>
 */
public class ASTFTCPClientTemplate extends ASTFClientTemplate {

  private int port = 80;
  private float cps = 1;
  private ASTFGlobalInfoPerTemplate globalInfoPerTemplate;
  private int limit;
  private boolean cont;

  public ASTFTCPClientTemplate(ASTFProgram program, ASTFIpGen ipGen) {
    this(program, ipGen, null, 80, 1, null, 0, false);
  }

  public ASTFTCPClientTemplate(ASTFProgram program, ASTFIpGen ipGen, int limit) {
    this(program, ipGen, null, 80, 1, null, limit, false);
  }

  public ASTFTCPClientTemplate(ASTFProgram program, ASTFIpGen ipGen, int port, int limit) {
    this(program, ipGen, null, port, 1, null, limit, false);
  }

  public ASTFTCPClientTemplate(
      ASTFProgram program, ASTFIpGen ipGen, int port, float cps, int limit) {
    this(program, ipGen, null, port, cps, null, limit, false);
  }

  public ASTFTCPClientTemplate(
      ASTFProgram program, ASTFIpGen ipGen, int port, int limit, boolean cont) {
    this(program, ipGen, null, port, 1, null, limit, cont);
  }

  public ASTFTCPClientTemplate(
      ASTFProgram program,
      ASTFIpGen ipGen,
      ASTFCluster cluster,
      int port,
      float cps,
      ASTFGlobalInfoPerTemplate globInfo,
      int limit) {
    this(program, ipGen, cluster, port, cps, globInfo, limit, false);
  }

  /**
   * constructor
   *
   * @param program L7 emulation program
   * @param ipGen ASTFIPGen
   * @param cluster ASTFCluster
   * @param port destination port
   * @param cps New connection per second rate. Minimal value is 0.5
   * @param globInfo ASTFGlobalInfoPerTemplate
   * @param limit limit the number of flows. default is None which means zero (there is no limit)
   * @param cont try to keep the number of flows up to limit.
   */
  public ASTFTCPClientTemplate(
      ASTFProgram program,
      ASTFIpGen ipGen,
      ASTFCluster cluster,
      int port,
      float cps,
      ASTFGlobalInfoPerTemplate globInfo,
      int limit,
      boolean cont) {
    super(ipGen, cluster, program);
    this.port = port;
    this.cps = cps;
    this.globalInfoPerTemplate = globInfo;
    this.limit = limit;
    this.cont = cont;
  }

  /**
   * to json format
   *
   * @return JsonObject
   */
  @Override
  public JsonObject toJson() {
    JsonObject jsonObject = super.toJson();
    jsonObject.addProperty("port", port);
    jsonObject.addProperty("cps", cps);
    if (limit > 0) {
      jsonObject.addProperty("limit", limit);
      if (cont) {
        jsonObject.addProperty("cont", cont);
      }
    }
    if (globalInfoPerTemplate != null) {
      jsonObject.add("glob_info", globalInfoPerTemplate.toJson());
    }
    return jsonObject;
  }
}
