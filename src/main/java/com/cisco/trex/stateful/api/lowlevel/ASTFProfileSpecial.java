package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Locate here temporary,will remove later.
 * This class provide a separate method to fetch cached json command. When user use it to create
 * profiles,all the profiles will not interact with each other.So user can create all the profiles
 * first and then start all of them.
 * This class have no impact on the class AstfProfile.
 * For creating one profile and then start it , we can use AstfProfile as before , and can also use
 * this Class.
 * For creating multi profile and then start all of them , we should use this class.
 */
public class ASTFProfileSpecial extends ASTFProfile {
    private JsonObject jsonObject = null;

    /**
     * Constructor
     *
     * @param defaultIpGen
     * @param astfTemplateList
     */
    public ASTFProfileSpecial(ASTFIpGen defaultIpGen, List<ASTFTemplate> astfTemplateList) {
        this(defaultIpGen, (ASTFGlobalInfo) null, (ASTFGlobalInfo) null, astfTemplateList, (List) null);
    }

    /**
     * Constructor
     *
     * @param defaultIpGen
     * @param astfClientGlobalInfo
     * @param astfServerGlobalInfo
     * @param astfTemplateList
     * @param astfCapInfoList
     */
    public ASTFProfileSpecial(ASTFIpGen defaultIpGen, ASTFGlobalInfo astfClientGlobalInfo,
                        ASTFGlobalInfo astfServerGlobalInfo, List<ASTFTemplate> astfTemplateList, List<ASTFCapInfo> astfCapInfoList) {
        super(defaultIpGen, astfClientGlobalInfo, astfServerGlobalInfo, astfTemplateList, astfCapInfoList);
        loadJsonObject();
    }

    private void loadJsonObject() {
        if (jsonObject == null) {
            jsonObject = super.toJson();
            clearCache();
        }
    }

    @Override
    public JsonObject toJson() {
        loadJsonObject();
        return jsonObject;
    }

}
