package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk ASTFTemplate class
 * 
 * ASTFTemplate class
 * <p>
 * One manual template
 * client commands
 *
 * @<code> ASTFProgram progC =new ASTFProgram();
 * progC.send(http_req);
 * progC.recv(http_response.length());
 * </code>
 * </p>
 *
 * <p>
 * ip generator
 * @<code> ASTFIpGenDist ipGenC =new ASTFIpGenDist("16.0.0.0", "16.0.0.255");
 * ASTFIpGenDist ipGenS =new ASTFIpGenDist("48.0.0.0", "48.0.255.255");
 * ASTFIpGen ipGen = new ASTFIpGen(new ASTFIpGenGlobal("1.0.0.0");
 * </code>
 * </p>
 *
 * <p>
 * template
 * @<code> ASTFTCPClientTemplate tempC=new ASTFTCPClientTemplate(progC, ipGen);
 * ASTFTCPServerTemplate tempS=new ASTFTCPServerTemplate(progC, ipGen);
 * ASTFTemplate astfTemplate=mew ASTFTemplate(tempC,tempS);
 * </code>
 * </p>
 */
public class ASTFTemplate {
    private ASTFTCPClientTemplate astfTcpClientTemplate;
    private ASTFTCPServerTemplate astfTcpServerTemplate;

    /**
     * construct
     *
     * @param astfTcpClientTemplate
     * @param astfTcpServerTemplate
     */
    public ASTFTemplate(ASTFTCPClientTemplate astfTcpClientTemplate, ASTFTCPServerTemplate astfTcpServerTemplate) {
        if (astfTcpClientTemplate.isStream() != astfTcpServerTemplate.isStream()) {
            throw new IllegalStateException(String.format(" Client template stream mode is %s and different from server template mode %s", astfTcpClientTemplate.isStream(), astfTcpServerTemplate.isStream()));
        }
        this.astfTcpClientTemplate = astfTcpClientTemplate;
        this.astfTcpServerTemplate = astfTcpServerTemplate;
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
        return json;
    }
}
