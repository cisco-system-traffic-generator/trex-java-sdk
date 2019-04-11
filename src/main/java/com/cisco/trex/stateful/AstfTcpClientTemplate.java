package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

/**
 * One manual template
 * <p>
 * client commands
 * @<code>
 *      AstfProgram progC =new AstfProgram()
 *      progC.send(http_req)
 *      progC.recv(http_response.length())
 * </code>
 * </p>
 *
 * <p>
 * ip generator
 * @<code>
 *      AstfIpGenDist ipGenC =new AstfIpGenDist("16.0.0.0", "16.0.0.255");
 *      AstfIpGenDist ipGenS =new AstfIpGenDist("48.0.0.0", "48.0.255.255");
 *      AstfIpGen ipGen = new AstfIpGen(new AstfIpGenGlobal("1.0.0.0");
 *</code>
 * </p>
 *
 * <p>
 * template
 * @<code> AstfTcpClientTemplate tempC=new AstfTcpClientTemplate(progC, ipGen)</code>
 * </p>
 */
public class AstfTcpClientTemplate extends AstfClientTemplate{

    private int port=80;
    private float cps=1;
    private AstfGlobalInfoPerTemplate globalInfoPerTemplate;
    private int limit;

    /**
     * construct
     * @param iPGen AstfIpGen generator
     * @param cluster AstfCluster
     * @param astfProgram AstfProgram L7 emulation program
     * @param port destination port
     * @param cps New connection per second rate. Minimal value is 0.5
     * @param globInfo AstfGlobalInfoPerTemplate
     * @param limit limit the number of flows. default is None which means zero (there is no limit)
     */
    public AstfTcpClientTemplate(AstfProgram astfProgram,AstfIpGen iPGen, AstfCluster cluster, int port, float cps,AstfGlobalInfoPerTemplate globInfo,int limit) {
        super(iPGen,cluster,astfProgram);
        this.port=port;
        this.cps=cps;
        this.limit=limit;
        this.globalInfoPerTemplate=globInfo;
    }

    /**
     * to json format
     * @return JsonObject
     */
    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("port", port);
        jsonObject.addProperty("cps", cps);
        if(limit>0){
            jsonObject.addProperty("limit", limit);
        }
        if (globalInfoPerTemplate!=null){
            jsonObject.add("glob_info", globalInfoPerTemplate.toJson());
        }
        return jsonObject;

    }
}
