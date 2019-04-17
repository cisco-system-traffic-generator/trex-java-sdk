package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

/**
 * Astf Tcp Server Template
 */
public class AstfTcpServerTemplate extends AstfTemplateBase {
    private AstfGlobalInfoPerTemplate globalInfo;
    private AstfAssociation assoc;

    /**
     * construct
     *
     * @param astfProgram
     */
    public AstfTcpServerTemplate(AstfProgram astfProgram) {
        this(astfProgram, null, null);
    }

    /**
     * construct
     *
     * @param astfProgram
     * @param assoc
     * @param globalInfo
     */
    public AstfTcpServerTemplate(AstfProgram astfProgram, AstfAssociation assoc, AstfGlobalInfoPerTemplate globalInfo) {
        super(astfProgram);
        if (assoc == null) {
            this.assoc = new AstfAssociation(new AstfAssociationRule(null, null, 80));
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
