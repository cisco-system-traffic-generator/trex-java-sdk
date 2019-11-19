package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Java implementation for TRex python sdk ASTFProfile class */
public class ASTFProfile {

  private static final Logger LOGGER = LoggerFactory.getLogger(ASTFProfile.class);

  private static final String L7_PRECENT = "l7_percent";
  private static final String CPS = "cps";

  private ASTFIpGen astfIpGen;
  private ASTFGlobalInfo astfClientGlobalInfo;
  private ASTFGlobalInfo astfServerGlobalInfo;
  private List<ASTFTemplate> astfTemplateList;
  private List<ASTFCapInfo> astfCapInfoList;

  private Map<String, Integer> tgName2TgId =
      new LinkedHashMap<>(); // template group name -> template group id

  /**
   * construct
   *
   * @param defaultIpGen
   * @param astfTemplateList
   */
  public ASTFProfile(ASTFIpGen defaultIpGen, List<ASTFTemplate> astfTemplateList) {
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
  public ASTFProfile(
      ASTFIpGen defaultIpGen,
      ASTFGlobalInfo astfClientGlobalInfo,
      ASTFGlobalInfo astfServerGlobalInfo,
      List<ASTFTemplate> astfTemplateList,
      List<ASTFCapInfo> astfCapInfoList) {
    this.astfClientGlobalInfo = astfClientGlobalInfo;
    this.astfServerGlobalInfo = astfServerGlobalInfo;

    if (astfTemplateList == null && astfCapInfoList == null) {
      throw new IllegalStateException(
          String.format(
              "bad param combination,AstfTemplate and AstfCapInfo should not be null at the same time "));
    }
    this.astfTemplateList = astfTemplateList;
    this.astfCapInfoList = astfCapInfoList;

    for (ASTFTemplate template : astfTemplateList) {
      if (template.getTgName() == null) {
        template.setTgId(0);
        continue;
      }

      String tgName = template.getTgName();
      if (!tgName2TgId.containsKey(tgName)) {
        tgName2TgId.put(tgName, tgName2TgId.size() + 1);
      }
      template.setTgId(tgName2TgId.get(tgName));
    }

    /** for pcap file parse scenario */
    if (astfCapInfoList != null && astfCapInfoList.size() != 0) {
      String mode = null;
      List<Map<String, Object>> allCapInfo = new ArrayList();
      List<Integer> dPorts = new ArrayList();
      int totalPayload = 0;
      for (ASTFCapInfo capInfo : astfCapInfoList) {
        String capFile = capInfo.getFilePath();
        ASTFIpGen ipGen = capInfo.getAstfIpGen() != null ? capInfo.getAstfIpGen() : defaultIpGen;
        ASTFGlobalInfoPerTemplate globC = capInfo.getClientGlobInfo();
        ASTFGlobalInfoPerTemplate globS = capInfo.getServerGlobInfo();
        ASTFProgram progC = new ASTFProgram(capFile, ASTFProgram.SideType.Client);
        ASTFProgram progS = new ASTFProgram(capFile, ASTFProgram.SideType.Server);
        progC.updateKeepAlive(progS);

        ASTFTCPInfo tcpC = new ASTFTCPInfo(capFile);
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
            throw new IllegalStateException(
                "If one cap specifies l7_percent, then all should specify it");
          }
          if (mode.equals(CPS) && l7Percent > 0) {
            throw new IllegalStateException(
                "Can't mix specifications of cps and l7_percent in same cap list");
          }
        }
        totalPayload += progC.getPayloadLen();

        int dPort;
        ASTFAssociation myAssoc;
        if (capInfo.getAssoc() == null) {
          dPort = tcpCPort;
          myAssoc = new ASTFAssociation(new ASTFAssociationRule(dPort));
        } else {
          dPort = capInfo.getAssoc().getPort();
          myAssoc = capInfo.getAssoc();
          throw new IllegalStateException(
              String.format(
                  "More than one cap use dest port %s. This is currently not supported.", dPort));
        }
        dPorts.add(dPort);

        /** add param to cap info map */
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

      // calculate cps from l7 percent
      if (mode.equals(L7_PRECENT)) {
        float percentSum = 0;
        for (Map<String, Object> map : allCapInfo) {
          float newCps = ((ASTFProgram) map.get("prog_c")).getPayloadLen() * 100.0f / totalPayload;
          map.put("cps", newCps);
          percentSum += newCps;
        }
        if (percentSum != 100) {
          throw new IllegalStateException("l7_percent values must sum up to 100");
        }
      }

      for (Map<String, Object> map : allCapInfo) {
        ASTFTCPClientTemplate tempC =
            new ASTFTCPClientTemplate(
                (ASTFProgram) map.get("prog_c"),
                (ASTFIpGen) map.get("ip_gen"),
                null,
                (int) map.get("d_port"),
                (float) map.get("cps"),
                (ASTFGlobalInfoPerTemplate) map.get("glob_c"),
                (int) map.get("limit"));
        ASTFTCPServerTemplate tempS =
            new ASTFTCPServerTemplate(
                (ASTFProgram) map.get("prog_s"),
                (ASTFAssociation) map.get("my_assoc"),
                (ASTFGlobalInfoPerTemplate) map.get("glob_s"));
        ASTFTemplate template = new ASTFTemplate(tempC, tempS);
        astfTemplateList.add(template);
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
    json.add("buf_list", ASTFProgram.classToJson());
    json.add("ip_gen_dist_list", ASTFIpGenDist.clssToJson());
    json.add("program_list", ASTFTemplateBase.classToJson());
    if (this.astfClientGlobalInfo != null) {
      json.add("c_glob_info", this.astfClientGlobalInfo.toJson());
    }
    if (this.astfServerGlobalInfo != null) {
      json.add("s_glob_info", this.astfServerGlobalInfo.toJson());
    }
    JsonArray jsonArray = new JsonArray();
    for (ASTFTemplate astfTemplate : astfTemplateList) {
      jsonArray.add(astfTemplate.toJson());
    }
    json.add("templates", jsonArray);

    JsonArray tgNames = new JsonArray();
    tgName2TgId.keySet().forEach(name -> tgNames.add(name));
    json.add("tg_names", tgNames);

    return json;
  }

  /** clear all cache data. */
  public static void clearCache() {
    ASTFProgram.classReset();
    ASTFIpGenDist.classReset();
    ASTFTemplateBase.classReset();
  }

  /** print stats */
  public void printStats() {
    float totalBps = 0;
    float totalCps = 0;
    LOGGER.info("Num buffers: {}", ASTFProgram.getBufSize());
    LOGGER.info("Num programs: {}", ASTFTemplateBase.programNum());
    for (int i = 0; i < astfTemplateList.size(); i++) {
      LOGGER.info("------------------------------");
      LOGGER.info("template {}:", i);
      JsonObject tempJson = astfTemplateList.get(i).toJson();
      int clientProgInd =
          tempJson.getAsJsonObject("client_template").get("program_index").getAsInt();
      int serverProgInd =
          tempJson.getAsJsonObject("server_template").get("program_index").getAsInt();
      int totalBytes =
          ASTFTemplateBase.getTotalSendBytes(clientProgInd)
              + ASTFTemplateBase.getTotalSendBytes(serverProgInd);

      float tempCps = tempJson.getAsJsonObject("client_template").get("cps").getAsFloat();
      float tempBps = totalBytes * tempCps * 8;
      LOGGER.info("total bytes:{} cps:{} bps(bytes * cps * 8):{}", totalBytes, tempCps, tempBps);
      totalBps += tempBps;
      totalCps += tempCps;
    }
    LOGGER.info("total for all templates - cps:{} bps:{}", totalCps, totalBps);
  }

  public List<ASTFTemplate> getAstfTemplateList() {
    return this.astfTemplateList;
  }
}
