package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk ASTFTCPClientTemplate class
 * 
 * One manual template
 * <p>
 * client commands
 *
 * <code> ASTFProgram progC =new ASTFProgram()
 * progC.send(http_req)
 * progC.recv(http_response.length())
 * </code>
 * </p>
 *
 * <p>
 * ip generator
 * <code> ASTFIpGenDist ipGenC =new ASTFIpGenDist("16.0.0.0", "16.0.0.255");
 * ASTFIpGenDist ipGenS =new ASTFIpGenDist("48.0.0.0", "48.0.255.255");
 * ASTFIpGen ipGen = new ASTFIpGen(new ASTFIpGenGlobal("1.0.0.0");
 * </code>
 * </p>
 *
 * <p>
 * template
 * <code> ASTFTCPClientTemplate tempC=new ASTFTCPClientTemplate(progC, ipGen)</code>
 * </p>
 */
public class ASTFTCPClientTemplate extends ASTFClientTemplate {

    private int port = 80;
    private float cps = 1;
    private ASTFGlobalInfoPerTemplate globalInfoPerTemplate;
    private int limit;

    /**
     * construct
     *
     * @param astfProgram
     * @param iPGen
     */
    public ASTFTCPClientTemplate(ASTFProgram astfProgram, ASTFIpGen iPGen) {
        this(astfProgram, iPGen, 0);
    }

    /**
     * construct
     *
     * @param astfProgram
     * @param iPGen
     * @param limit
     */
    public ASTFTCPClientTemplate(ASTFProgram astfProgram, ASTFIpGen iPGen, int limit) {
        this(astfProgram, iPGen, 80, limit);
    }

    /**
     * construct
     *
     * @param astfProgram
     * @param iPGen
     * @param port
     * @param limit
     */
    public ASTFTCPClientTemplate(ASTFProgram astfProgram, ASTFIpGen iPGen, int port, int limit) {
        this(astfProgram, iPGen, null, port, 1, null, limit);
    }

    /**
     * construct
     *
     * @param iPGen       AstfIpGen generator
     * @param cluster     AstfCluster
     * @param astfProgram AstfProgram L7 emulation program
     * @param port        destination port
     * @param cps         New connection per second rate. Minimal value is 0.5
     * @param globInfo    AstfGlobalInfoPerTemplate
     * @param limit       limit the number of flows. default is None which means zero (there is no limit)
     */
    public ASTFTCPClientTemplate(ASTFProgram astfProgram, ASTFIpGen iPGen, ASTFCluster cluster, int port, float cps, ASTFGlobalInfoPerTemplate globInfo, int limit) {
        super(iPGen, cluster, astfProgram);
        this.port = port;
        this.cps = cps;
        this.limit = limit;
        this.globalInfoPerTemplate = globInfo;
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
        }
        if (globalInfoPerTemplate != null) {
            jsonObject.add("glob_info", globalInfoPerTemplate.toJson());
        }
        return jsonObject;
    }
}
