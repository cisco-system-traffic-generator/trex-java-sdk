package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/** Java implementation for TRex python sdk ASTFTCPServerTemplate class */
public class ASTFTCPServerTemplate extends ASTFTemplateBase {
  private ASTFGlobalInfoPerTemplate globInfo;
  private ASTFAssociation association;

  public ASTFTCPServerTemplate(
      ASTFProgram program, ASTFAssociation association, ASTFGlobalInfoPerTemplate globInfo) {
    super(program);
    if (association == null) {
      this.association = new ASTFAssociation(new ASTFAssociationRule(80));
    } else {
      this.association = association;
    }
    this.globInfo = globInfo;
  }

  public ASTFAssociation getAssociation() {
    return association;
  }

  public JsonObject toJson() {
    JsonObject jsonObject = super.toJson();
    jsonObject.add("assoc", association.toJson());
    if (globInfo != null) {
      jsonObject.add("glob_info", globInfo.toJson());
    }
    return jsonObject;
  }
}
