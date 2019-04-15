package com.cisco.trex.stateful;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ASTF profile
 */
public class AstfProfile {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AstfProfile.class);

    private AstfIpGen astfIpGen;
    private AstfGlobalInfo astfClientGlobalInfo;
    private AstfGlobalInfo astfServerGlobalInfo;
    private List<AstfTemplate> astfTemplateList;
    private List<AstfCapInfo> astfCapInfoList;

    /**
     * construct
     *
     * @param defaultIpGen
     * @param astfClientGlobalInfo
     * @param astfServerGlobalInfo
     * @param astfTemplateList
     * @param astfCapInfoList
     */
    public AstfProfile(AstfIpGen defaultIpGen, AstfGlobalInfo astfClientGlobalInfo, AstfGlobalInfo astfServerGlobalInfo, List<AstfTemplate> astfTemplateList, List<AstfCapInfo> astfCapInfoList) {
        this.astfClientGlobalInfo = astfClientGlobalInfo;
        this.astfServerGlobalInfo = astfServerGlobalInfo;

        if (astfTemplateList == null && astfCapInfoList == null) {
            throw new IllegalStateException(String.format("bad param combination,AstfTemplate and AstfCapInfo should not be null at the same time "));
        }
        this.astfTemplateList = astfTemplateList;
        this.astfCapInfoList = astfCapInfoList;

        /**
         * for pcap file scenario
         * TODO: need to be implemented in the future
         */
        if (astfCapInfoList != null && astfCapInfoList.size() != 0) {

            String mode = null;
            Map<String, Object> allCapInfo = new HashMap();
            List<Integer> dPorts = new ArrayList();
            int totalPayload = 0;
            for (AstfCapInfo capInfo : astfCapInfoList) {
                String capFile = capInfo.getFilePath();
                AstfIpGen ipGen = capInfo.getAstfIpGen() != null ? capInfo.getAstfIpGen() : defaultIpGen;
                AstfGlobalInfoPerTemplate globC = capInfo.getClientGlobInfo();
                AstfGlobalInfoPerTemplate globS = capInfo.getServerGlobInfo();
                AstfProgram progC = new AstfProgram(new File(capFile), AstfProgram.SideType.Client, null, true);
                AstfProgram progS = new AstfProgram(new File(capFile), AstfProgram.SideType.Server, null, true);
                progC.updateKeepAlive(progS);
                //TODO: analysis pcap file data.
            }

        }
    }

    /**
     * to json format
     *
     * @return json string
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("buf_list", AstfProgram.classToJson());
        json.add("ip_gen_dist_list", AstfIpGenDist.clssToJson());
        json.add("program_list", AstfTemplateBase.classToJson());
        if (this.astfClientGlobalInfo != null) {
            json.add("c_glob_info", this.astfClientGlobalInfo.toJson());
        }
        if (this.astfServerGlobalInfo != null) {
            json.add("s_glob_info", this.astfServerGlobalInfo.toJson());
        }
        JsonArray jsonArray = new JsonArray();
        for (AstfTemplate astfTemplate : astfTemplateList) {
            jsonArray.add(astfTemplate.toJson());
        }
        json.add("templates", jsonArray);
        return json;
    }

    /**
     * print stats
     */
    public void printStats() {
        float totalBps = 0;
        float totalCps = 0;
        LOGGER.info("Num buffers: {}", AstfProgram.getBufSize());
        LOGGER.info("Num programs: {}", AstfTemplateBase.programNum());
        for (int i = 0; i < astfTemplateList.size(); i++) {
            LOGGER.info("------------------------------");
            LOGGER.info("template {}:", i);
            JsonObject tempJson = astfTemplateList.get(i).toJson();
            int clientProgInd = tempJson.getAsJsonObject("client_template").get("program_index").getAsInt();
            int serverProgInd = tempJson.getAsJsonObject("server_template").get("program_index").getAsInt();
            int totalBytes = AstfTemplateBase.getTotalSendBytes(clientProgInd) + AstfTemplateBase.getTotalSendBytes(serverProgInd);

            float tempCps = tempJson.getAsJsonObject("client_template").get("cps").getAsFloat();
            float tempBps = totalBytes * tempCps * 8;
            LOGGER.info("total bytes:{} cps:{} bps(bytes * cps * 8):{}", totalBytes, tempCps, tempBps);
            totalBps += tempBps;
            totalCps += tempCps;
        }
        LOGGER.info("total for all templates - cps:{} bps:{}", totalCps, totalBps);
    }

}
