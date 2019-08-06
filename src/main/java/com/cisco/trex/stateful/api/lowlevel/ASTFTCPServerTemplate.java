package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk ASTFTCPServerTemplate class
 */
public class ASTFTCPServerTemplate extends ASTFTemplateBase {
    private ASTFGlobalInfoPerTemplate globalInfo;
    private ASTFAssociation assoc;

    /**
     * construct
     *
     * @param astfProgram
     */
    public ASTFTCPServerTemplate(ASTFProgram astfProgram) {
        this(astfProgram, null, null);
    }

    /**
     * construct
     *
     * @param astfProgram
     * @param assoc
     * @param globalInfo
     */
    public ASTFTCPServerTemplate(ASTFProgram astfProgram, ASTFAssociation assoc, ASTFGlobalInfoPerTemplate globalInfo) {
        super(astfProgram);
        if (assoc == null) {
            this.assoc = new ASTFAssociation(new ASTFAssociationRule(80));
        } else {
            this.assoc = assoc;
        }
        this.globalInfo = globalInfo;

    }

    /**
     * to json format
     *
     * @return JsonObject
     */
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("assoc", assoc.toJson());
        if (globalInfo != null) {
            json.add("glob_info", globalInfo.toJson());
        }
        return json;
    }
}
