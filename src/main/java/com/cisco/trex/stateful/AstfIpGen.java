package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

/**
 * Astf Ip Generator
 */
public class AstfIpGen {
    private AstfIpGenDist distClient;
    private AstfIpGenDist distServer;
    private AstfIpGenGlobal ipGenGlobal;

    private JsonObject fields=new JsonObject();

    /**
     * construct
     * @param distClient
     * @param distServer
     * @param ipGenGlobal
     */
    public AstfIpGen(AstfIpGenDist distClient,AstfIpGenDist distServer,AstfIpGenGlobal ipGenGlobal){
        this.distClient=distClient;
        this.distServer=distServer;
        this.ipGenGlobal=ipGenGlobal;

        this.fields.add("dist_client", distClient.toJson());
        distClient.setDirection("c");
        distClient.setIpOffset(ipGenGlobal.getIpOffset());

        this.fields.add("dist_server", distServer.toJson());
        distServer.setDirection("s");
        distServer.setIpOffset(ipGenGlobal.getIpOffset());
    }

    /**
     * to json format
     * @return JsonObject
     */
    public JsonObject toJson(){
        return fields;
    }

}
