package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk _ASTFClientTemplate class, the abstract Astf Client
 * Template class
 */
abstract class ASTFClientTemplate extends ASTFTemplateBase {
  private ASTFCluster astfCluster;
  private ASTFIpGen iPGen;
  private ASTFProgram astfProgram;

  /**
   * construct
   *
   * @param iPGen
   * @param astfCluster
   * @param astfProgram
   */
  public ASTFClientTemplate(ASTFIpGen iPGen, ASTFCluster astfCluster, ASTFProgram astfProgram) {
    super(astfProgram);
    this.iPGen = iPGen;
    this.astfCluster = astfCluster == null ? new ASTFCluster() : astfCluster;
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
