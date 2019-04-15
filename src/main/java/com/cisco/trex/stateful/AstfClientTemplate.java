package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

/**
 * abstract Astf Client Template class
 */
abstract class AstfClientTemplate extends AstfTemplateBase {
    private AstfCluster astfCluster;
    private AstfIpGen iPGen;
    private AstfProgram astfProgram;

    /**
     * construct
     *
     * @param iPGen
     * @param astfCluster
     * @param astfProgram
     */
    public AstfClientTemplate(AstfIpGen iPGen, AstfCluster astfCluster, AstfProgram astfProgram) {
        super(astfProgram);
        this.iPGen = iPGen;
        this.astfCluster = astfCluster == null ? new AstfCluster() : astfCluster;
        this.astfProgram = astfProgram;
    }

    /**
     * to json format
     *
     * @return JsonObject
     */
    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("ip_gen", iPGen.toJson());
        json.add("cluster", astfCluster.toJson());
        return json;
    }
}
