package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;

/** Java implementation for TRex python sdk ASTFAssociation class */
public class ASTFAssociation {

  private List<ASTFAssociationRule> astfAssociationRuleList;

  /**
   * constructor
   *
   * @param astfAssociationRuleList
   */
  public ASTFAssociation(List<ASTFAssociationRule> astfAssociationRuleList) {
    this.astfAssociationRuleList = astfAssociationRuleList;
  }

  /**
   * constructor
   *
   * @param astfAssociationRule
   */
  public ASTFAssociation(ASTFAssociationRule astfAssociationRule) {
    this.astfAssociationRuleList = new ArrayList<>();
    this.astfAssociationRuleList.add(astfAssociationRule);
  }

  /**
   * to Json format
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
   * get port
   *
   * @return port
   */
  public int getPort() {
    if (astfAssociationRuleList.size() != 1) {
      throw new IllegalStateException(
          String.format(
              "rule list size should be 1, but it's %s now", astfAssociationRuleList.size()));
    }
    return astfAssociationRuleList.get(0).getPort();
  }

  /**
   * check if only have port field
   *
   * @return true if only have port field ,otherwise return false
   */
  public boolean isPortOnly() {
    if (astfAssociationRuleList.size() != 1) {
      throw new IllegalStateException(
          String.format(
              "rule list size should be 1, but it's %s now", astfAssociationRuleList.size()));
    }
    return astfAssociationRuleList.get(0).toJson().entrySet().size() == 1;
  }
}
