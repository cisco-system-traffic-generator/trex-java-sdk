package com.cisco.trex.stateful;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Astf Association
 */
public class AstfAssociation {

    private List<AstfAssociationRule> astfAssociationRuleList;

    /**
     * construct
     *
     * @param astfAssociationRuleList
     */
    public AstfAssociation(List<AstfAssociationRule> astfAssociationRuleList) {
        this.astfAssociationRuleList = astfAssociationRuleList;
    }

    /**
     * construct
     *
     * @param astfAssociationRule
     */
    public AstfAssociation(AstfAssociationRule astfAssociationRule) {
        astfAssociationRuleList = new ArrayList();
        astfAssociationRuleList.add(astfAssociationRule);
    }

    /**
     * to json format
     *
     * @return JsonArray
     */
    public JsonArray toJson() {
        JsonArray jsonArray = new JsonArray();
        for (AstfAssociationRule rule : astfAssociationRuleList) {
            jsonArray.add(rule.toJson());
        }
        return jsonArray;
    }

    /**
     * get Port
     *
     * @return port
     */
    public int getPort() {
        if (astfAssociationRuleList.size() != 1) {
            throw new IllegalStateException(String.format("rule list size should be 1, but it's %s now", astfAssociationRuleList.size()));
        }
        return astfAssociationRuleList.get(0).getPort();
    }

}
