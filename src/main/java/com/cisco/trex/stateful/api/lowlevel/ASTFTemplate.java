package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk ASTFTemplate class
 *
 * <p>ASTFTemplate class
 *
 * <p>One manual template client commands <code> ASTFProgram progC =new ASTFProgram();
 * progC.send(http_req);
 * progC.recv(http_response.length());
 * </code>
 *
 * <p>ip generator <code> ASTFIpGenDist ipGenC =new ASTFIpGenDist("16.0.0.0", "16.0.0.255");
 * ASTFIpGenDist ipGenS =new ASTFIpGenDist("48.0.0.0", "48.0.255.255");
 * ASTFIpGen ipGen = new ASTFIpGen(new ASTFIpGenGlobal("1.0.0.0");
 * </code>
 *
 * <p>template <code> ASTFTCPClientTemplate tempC=new ASTFTCPClientTemplate(progC, ipGen);
 * ASTFTCPServerTemplate tempS=new ASTFTCPServerTemplate(progC, ipGen);
 * ASTFTemplate astfTemplate=mew ASTFTemplate(tempC,tempS);
 * </code>
 */
public class ASTFTemplate {

  private ASTFTCPClientTemplate astfTcpClientTemplate;
  private ASTFTCPServerTemplate astfTcpServerTemplate;
  private String tgName;
  private Integer tgId;

  /**
   * construct
   *
   * @param astfTcpClientTemplate
   * @param astfTcpServerTemplate
   */
  public ASTFTemplate(
      ASTFTCPClientTemplate astfTcpClientTemplate, ASTFTCPServerTemplate astfTcpServerTemplate) {
    this(astfTcpClientTemplate, astfTcpServerTemplate, null);
  }

  /**
   * construct
   *
   * @param astfTcpClientTemplate
   * @param astfTcpServerTemplate
   * @param tgName
   */
  public ASTFTemplate(
      ASTFTCPClientTemplate astfTcpClientTemplate,
      ASTFTCPServerTemplate astfTcpServerTemplate,
      String tgName) {
    if (astfTcpClientTemplate.isStream() != astfTcpServerTemplate.isStream()) {
      throw new IllegalStateException(
          String.format(
              " Client template stream mode is %s and different from server template mode %s",
              astfTcpClientTemplate.isStream(), astfTcpServerTemplate.isStream()));
    }

    if (tgName != null && (tgName.length() > 20 || tgName.isEmpty())) {
      throw new IllegalArgumentException(String.format("tgName %s is empty or too long", tgName));
    }

    this.astfTcpClientTemplate = astfTcpClientTemplate;
    this.astfTcpServerTemplate = astfTcpServerTemplate;
    this.tgName = tgName;
  }

  public String getTgName() {
    return tgName;
  }

  Integer getTgId() {
    return tgId;
  }

  void setTgId(Integer tgId) {
    this.tgId = tgId;
  }

  /**
   * to json format
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("client_template", astfTcpClientTemplate.toJson());
    json.add("server_template", astfTcpServerTemplate.toJson());
    json.addProperty("tg_id", tgId);
    return json;
  }
}
