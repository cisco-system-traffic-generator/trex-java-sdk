package com.cisco.trex.stateful.api.lowlevel;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;

/** Java implementation for TRex python sdk ASTFAssociation class */
public class ASTFAssociation {

  private List<ASTFAssociationRule> astfAssociationRuleList;

  /**
   * construct
   *
   * @param astfAssociationRuleList
   */
  public ASTFAssociation(List<ASTFAssociationRule> astfAssociationRuleList) {
    this.astfAssociationRuleList = astfAssociationRuleList;
  }

  /**
   * construct
   *
   * @param astfAssociationRule
   */
  public ASTFAssociation(ASTFAssociationRule astfAssociationRule) {
    astfAssociationRuleList = new ArrayList<>();
    astfAssociationRuleList.add(astfAssociationRule);
  }

  /**
   * to json format
   *
   * @return JsonArray
   */
  public JsonArray toJson() {
    JsonArray jsonArray = new JsonArray();
    for (ASTFAssociationRule rule : astfAssociationRuleList) {
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
      throw new IllegalStateException(String.format("rule list size should be 1, but it's %s now",
          astfAssociationRuleList.size()));
    }
    return astfAssociationRuleList.get(0).getPort();
  }
}
