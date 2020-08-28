package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

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
  private static final String DEFAULT_TG_NAME = "defaultTgName";

  public ASTFTemplate(
      ASTFTCPClientTemplate astfTcpClientTemplate, ASTFTCPServerTemplate astfTcpServerTemplate) {
    this(astfTcpClientTemplate, astfTcpServerTemplate, DEFAULT_TG_NAME);
  }

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

    if (StringUtils.isEmpty(tgName)) {
      tgName = DEFAULT_TG_NAME;
    }
    if (tgName.length() > 20) {
      throw new IllegalStateException("tgName is longer than 20");
    }
    this.astfTcpClientTemplate = astfTcpClientTemplate;
    this.astfTcpServerTemplate = astfTcpServerTemplate;
    this.tgName = tgName;
  }

  public String getTgName() {
    return tgName;
  }

  public void setTgId(int tgId) {
    this.tgId = tgId;
  }

  public int getTgId() {
    return tgId;
  }

  public ASTFTCPClientTemplate getAstfTcpClientTemplate() {
    return astfTcpClientTemplate;
  }

  public ASTFTCPServerTemplate getAstfTcpServerTemplate() {
    return astfTcpServerTemplate;
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("client_template", astfTcpClientTemplate.toJson());
    jsonObject.add("server_template", astfTcpServerTemplate.toJson());
    if (this.tgId != 0) {
      jsonObject.addProperty("tg_id", tgId);
    }
    return jsonObject;
  }
}
