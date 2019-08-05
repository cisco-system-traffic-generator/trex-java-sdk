package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

/**
 * AstfTemplate class
 * <p>
 * One manual template
 * client commands
 *
 * @<code> AstfProgram progC =new AstfProgram();
 * progC.send(http_req);
 * progC.recv(http_response.length());
 * </code>
 * </p>
 *
 * <p>
 * ip generator
 * @<code> AstfIpGenDist ipGenC =new AstfIpGenDist("16.0.0.0", "16.0.0.255");
 * AstfIpGenDist ipGenS =new AstfIpGenDist("48.0.0.0", "48.0.255.255");
 * AstfIpGen ipGen = new AstfIpGen(new AstfIpGenGlobal("1.0.0.0");
 * </code>
 * </p>
 *
 * <p>
 * template
 * @<code> AstfTcpClientTemplate tempC=new AstfTcpClientTemplate(progC, ipGen);
 * AstfTcpServerTemplate tempS=new AstfTcpServerTemplate(progC, ipGen);
 * AstfTemplate astfTemplate=mew AstfTemplate(tempC,tempS);
 * </code>
 * </p>
 */
public class AstfTemplate {
    private AstfTcpClientTemplate astfTcpClientTemplate;
    private AstfTcpServerTemplate astfTcpServerTemplate;

    /**
     * construct
     *
     * @param astfTcpClientTemplate
     * @param astfTcpServerTemplate
     */
    public AstfTemplate(AstfTcpClientTemplate astfTcpClientTemplate, AstfTcpServerTemplate astfTcpServerTemplate) {
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
