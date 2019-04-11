package com.cisco.trex.stateful;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ASTF profile
 */
public class AstfProfile {
    private AstfIpGen astfIpGen;
    private AstfGlobalInfo astfClientGlobalInfo;
    private AstfGlobalInfo astfServerGlobalInfo;
    private List<AstfTemplate> astfTemplateList;
    private List<AstfCapInfo> astfCapInfoList;

    /**
     * construct
     * @param defaultIpGen
     * @param astfClientGlobalInfo
     * @param astfServerGlobalInfo
     * @param astfTemplateList
     * @param astfCapInfoList
     */
    public AstfProfile(AstfIpGen defaultIpGen, AstfGlobalInfo astfClientGlobalInfo, AstfGlobalInfo astfServerGlobalInfo,  List<AstfTemplate> astfTemplateList, List<AstfCapInfo> astfCapInfoList) {
        this.astfClientGlobalInfo = astfClientGlobalInfo;
        this.astfServerGlobalInfo = astfServerGlobalInfo;

        if (astfTemplateList.size()==0 && astfCapInfoList==null){
            throw new IllegalStateException(String.format("bad param combination,AstfTemplate and AstfCapInfo should not be existed at the same time "));
        }
        this.astfTemplateList = astfTemplateList;
        this.astfCapInfoList = astfCapInfoList;

        /**
         * for pcap file scenario
         * TODO: need to be implemented in the future
         */
        if(astfCapInfoList!=null&&astfCapInfoList.size()!=0){

            String mode=null;
            Map<String,Object> allCapInfo=new HashMap();
            List<Integer> dPorts=new ArrayList();
            int totalPayload = 0;
            for (AstfCapInfo capInfo : astfCapInfoList){
                String capFile = capInfo.getFilePath();
                AstfIpGen ipGen = capInfo.getAstfIpGen()!=null?capInfo.getAstfIpGen():defaultIpGen;
                AstfGlobalInfoPerTemplate globC = capInfo.getClientGlobInfo();
                AstfGlobalInfoPerTemplate globS = capInfo.getServerGlobInfo();
                AstfProgram progC = new AstfProgram(new File(capFile), AstfProgram.SideType.Client, null, true);
                AstfProgram progS = new AstfProgram(new File(capFile), AstfProgram.SideType.Server, null, true);
                progC.updateKeepAlive(progS);
            }

        }
    }

    /**
     * to json format
     * @return json string
     */
    public JsonObject toJson(){
        JsonObject json=new JsonObject();
        json.add("buf_list", AstfProgram.classToJson());
        json.add("ip_gen_dist_list", AstfIpGenDist.clssToJson());
        json.add("program_list", AstfTemplateBase.classToJson());
        if (this.astfClientGlobalInfo!=null){
            json.add("c_glob_info", this.astfClientGlobalInfo.toJson());
        }
        if (this.astfServerGlobalInfo!=null){
            json.add("s_glob_info", this.astfServerGlobalInfo.toJson());
        }
        JsonArray jsonArray=new JsonArray();
        for (AstfTemplate astfTemplate:astfTemplateList){
            jsonArray.add(astfTemplate.toJson());
        }
        json.add("templates", jsonArray);
        return json;
    }

}
