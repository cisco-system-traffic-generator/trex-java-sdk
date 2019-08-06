package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk ASTFIpGen class
 */
public class ASTFIpGen {
    private ASTFIpGenDist distClient;
    private ASTFIpGenDist distServer;
    private ASTFIpGenGlobal ipGenGlobal;

    private JsonObject fields = new JsonObject();

    /**
     * construct
     *
     * @param distClient
     * @param distServer
     * @param ipGenGlobal
     */
    public ASTFIpGen(ASTFIpGenDist distClient, ASTFIpGenDist distServer, ASTFIpGenGlobal ipGenGlobal) {
        this.distClient = distClient;
        this.distServer = distServer;
        this.ipGenGlobal = ipGenGlobal;

        this.fields.add("dist_client", distClient.toJson());
        distClient.setDirection("c");
        distClient.setIpOffset(ipGenGlobal.getIpOffset());

        this.fields.add("dist_server", distServer.toJson());
        distServer.setDirection("s");
        distServer.setIpOffset(ipGenGlobal.getIpOffset());
    }

    /**
     * to json format
     *
     * @return JsonObject
     */
    public JsonObject toJson() {
        return fields;
    }

}
