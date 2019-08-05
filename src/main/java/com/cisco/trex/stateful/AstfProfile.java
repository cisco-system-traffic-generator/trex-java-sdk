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

    private static final Logger LOGGER = LoggerFactory.getLogger(AstfProfile.class);

    private static final String L7_PRECENT = "l7_percent";
    private static final String CPS = "cps";

    private AstfIpGen astfIpGen;
    private AstfGlobalInfo astfClientGlobalInfo;
    private AstfGlobalInfo astfServerGlobalInfo;
    private List<AstfTemplate> astfTemplateList;
    private List<AstfCapInfo> astfCapInfoList;
    private String profileId;

    /**
     * construct
     *
     * @param defaultIpGen
     * @param astfTemplateList
     */
    public AstfProfile(AstfIpGen defaultIpGen, List<AstfTemplate> astfTemplateList) {
        this(defaultIpGen, null, null, astfTemplateList, null);
    }

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
         * for pcap file parse scenario
         */
        if (astfCapInfoList != null && astfCapInfoList.size() != 0) {
            String mode = null;
            List<Map<String, Object>> allCapInfo = new ArrayList();
            List<Integer> dPorts = new ArrayList();
            int totalPayload = 0;
            for (AstfCapInfo capInfo : astfCapInfoList) {
                String capFile = capInfo.getFilePath();
                AstfIpGen ipGen = capInfo.getAstfIpGen() != null ? capInfo.getAstfIpGen() : defaultIpGen;
                AstfGlobalInfoPerTemplate globC = capInfo.getClientGlobInfo();
                AstfGlobalInfoPerTemplate globS = capInfo.getServerGlobInfo();
                AstfProgram progC = new AstfProgram(capFile, AstfProgram.SideType.Client);
                AstfProgram progS = new AstfProgram(capFile, AstfProgram.SideType.Server);
                progC.updateKeepAlive(progS);

                AstfTcpInfo tcpC = new AstfTcpInfo(capFile);
                int tcpCPort = tcpC.getPort();
                float cps = capInfo.getCps();
                float l7Percent = capInfo.getL7Percent();
                if (mode == null) {
                    if (l7Percent > 0) {
                        mode = L7_PRECENT;
                    } else {
                        mode = CPS;
                    }
                } else {
                    if (mode.equals(L7_PRECENT) && l7Percent == 0) {
                        throw new IllegalStateException("If one cap specifies l7_percent, then all should specify it");
                    }
                    if (mode.equals(CPS) && l7Percent > 0) {
                        throw new IllegalStateException("Can't mix specifications of cps and l7_percent in same cap list");
                    }
                }
                totalPayload += progC.getPayloadLen();

                int dPort;
                AstfAssociation myAssoc;
                if (capInfo.getAssoc() == null) {
                    dPort = tcpCPort;
                    myAssoc = new AstfAssociation(new AstfAssociationRule(dPort));
                } else {
                    dPort = capInfo.getAssoc().getPort();
                    myAssoc = capInfo.getAssoc();
                    throw new IllegalStateException(String.format("More than one cap use dest port %s. This is currently not supported.", dPort));
                }
                dPorts.add(dPort);

                /**
                 * add param to cap info map
                 */
                HashMap<String, Object> map = new HashMap();
                map.put("ip_gen", ipGen);
                map.put("prog_c", progC);
                map.put("prog_s", progS);
                map.put("glob_c", globC);
                map.put("glob_s", globS);
                map.put("cps", cps);
                map.put("d_port", dPort);
                map.put("my_assoc", myAssoc);
                map.put("limit", capInfo.getLimit());
                allCapInfo.add(map);
            }

            //calculate cps from l7 percent
            if (mode.equals(L7_PRECENT)) {
                float percentSum = 0;
                for (Map<String, Object> map : allCapInfo) {
                    float newCps = ((AstfProgram) map.get("prog_c")).getPayloadLen() * 100.0f / totalPayload;
                    map.put("cps", newCps);
                    percentSum += newCps;
                }
                if (percentSum != 100) {
                    throw new IllegalStateException("l7_percent values must sum up to 100");
                }
            }

            for (Map<String, Object> map : allCapInfo) {
                AstfTcpClientTemplate tempC = new AstfTcpClientTemplate((AstfProgram) map.get("prog_c"), (AstfIpGen) map.get("ip_gen"), null,
                        (int) map.get("d_port"), (float) map.get("cps"), (AstfGlobalInfoPerTemplate) map.get("glob_c"), (int) map.get("limit"));
                AstfTcpServerTemplate tempS = new AstfTcpServerTemplate((AstfProgram) map.get("prog_s"), (AstfAssociation) map.get("my_assoc"), (AstfGlobalInfoPerTemplate) map.get("glob_s"));
                AstfTemplate template = new AstfTemplate(tempC, tempS);
                astfTemplateList.add(template);
            }
        }

        this.profileId = "astf_profile_" + System.currentTimeMillis();
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
     * clear all cache data.
     */
    public static void clearCache() {
        AstfProgram.classReset();
        AstfIpGenDist.classReset();
        AstfTemplateBase.classReset();
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
            int totalBytes = AstfTemplateBase.getTotalSendBytes(clientProgInd)
                    + AstfTemplateBase.getTotalSendBytes(serverProgInd);

            float tempCps = tempJson.getAsJsonObject("client_template").get("cps").getAsFloat();
            float tempBps = totalBytes * tempCps * 8;
            LOGGER.info("total bytes:{} cps:{} bps(bytes * cps * 8):{}", totalBytes, tempCps, tempBps);
            totalBps += tempBps;
            totalCps += tempCps;
        }
        LOGGER.info("total for all templates - cps:{} bps:{}", totalCps, totalBps);
    }

    public String getProfileId() {
        return this.profileId;
    }

}
