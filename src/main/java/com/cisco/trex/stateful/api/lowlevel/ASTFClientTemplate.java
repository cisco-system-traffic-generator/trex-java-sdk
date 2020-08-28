package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk _ASTFClientTemplate class, the abstract Astf Client
 * Template class
 */
abstract class ASTFClientTemplate extends ASTFTemplateBase {
  private ASTFCluster cluster;
  private ASTFIpGen ipGen;

  public ASTFClientTemplate(ASTFIpGen ipGen, ASTFCluster cluster, ASTFProgram program) {
    super(program);
    fields.add("ip_gen", ipGen.toJson());
    this.ipGen = ipGen;
    this.cluster = cluster == null ? new ASTFCluster() : cluster;
    fields.add("cluster", this.cluster.toJson());
  }

  @Override
  public JsonObject toJson() {
    JsonObject object = super.toJson();
    object.add("ip_gen", ipGen.toJson());
    object.add("cluster", this.cluster.toJson());
    return object;
  }

  public ASTFIpGen getIpGen() {
    return ipGen;
  }
}
