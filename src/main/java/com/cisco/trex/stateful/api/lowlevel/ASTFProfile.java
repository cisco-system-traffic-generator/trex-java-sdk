package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Java implementation for TRex python sdk ASTFProfile class */
public class ASTFProfile {
  private static final Logger LOGGER = LoggerFactory.getLogger(ASTFProfile.class);

  private static final String L7_PRECENT = "l7_percent";
  private static final String CPS = "cps";
  private ASTFGlobalInfo astfClientGlobalInfo;
  private ASTFGlobalInfo astfServerGlobalInfo;
  private List<ASTFTemplate> astfTemplateList;
  private Map<String, Integer> tgName2TgId = new LinkedHashMap<>(); // template group name ->
  // template group id
  private ASTFProfileCache astfProfileCache = new ASTFProfileCache(this);

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
   * constructor
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
    createASTFProfile(
        defaultIpGen,
        astfClientGlobalInfo,
        astfServerGlobalInfo,
        astfTemplateList,
        astfCapInfoList,
        null,
        null);
  }

  /**
   * constructor Define a ASTF profile You should give at least a template or a cap_list, maybe
   * both.
   *
   * @param defaultIpGen ASTFIPGen
   * @param astfClientGlobalInfo ASTFGlobalInfo tcp parameters to be used for client side, if
   *     cap_list is given. This is optional. If not specified,TCP parameters for each flow will be
   *     taken from its cap file.
   * @param astfServerGlobalInfo Same as default_tcp_server_info for client side.
   * @param astfTemplateList define a list of manual templates or one template
   * @param astfCapInfoList define a list of pcap files list in case there is no templates
   * @param sDelay ASTFCmdDelay :class:`trex.astf.trex_astf_profile.ASTFCmdDelay` Server delay
   *     command before sending response back to client. This will be applied on all cap in cap
   *     list, unless cap specified his own s_delay. defaults to None means no delay.
   * @param udpMtu int or None MTU for udp packets, if packets exceeding the specified value they
   *     will be cut down from L7 in order to fit. This will be applied on all cap in cap list,
   *     unless cap specified his own udp_mtu. defaults to None.
   */
  public ASTFProfile(
      ASTFIpGen defaultIpGen,
      ASTFGlobalInfo astfClientGlobalInfo,
      ASTFGlobalInfo astfServerGlobalInfo,
      List<ASTFTemplate> astfTemplateList,
      List<ASTFCapInfo> astfCapInfoList,
      ASTFCmdDelay sDelay,
      Integer udpMtu) {
    createASTFProfile(
        defaultIpGen,
        astfClientGlobalInfo,
        astfServerGlobalInfo,
        astfTemplateList,
        astfCapInfoList,
        sDelay,
        udpMtu);
  }

  /**
   * constructor Define a ASTF profile You should give at least a template or a cap_list, maybe
   * both.
   *
   * @param defaultIpGen ASTFIPGen
   * @param astfClientGlobalInfo ASTFGlobalInfo tcp parameters to be used for client side, if
   *     cap_list is given. This is optional. If not specified,TCP parameters for each flow will be
   *     taken from its cap file.
   * @param astfServerGlobalInfo Same as default_tcp_server_info for client side.
   * @param astfTemplateList define a list of manual templates or one template
   * @param astfCapInfoList define a list of pcap files list in case there is no templates
   * @param sDelay ASTFCmdDelayRnd see :class:`trex.astf.trex_astf_profile.ASTFCmdDelayRnd` Server
   *     delay command before sending response back to client. This will be applied on all cap in
   *     cap list, unless cap specified his own s_delay. defaults to None means no delay.
   * @param udpMtu int or None MTU for udp packets, if packets exceeding the specified value they
   *     will be cut down from L7 in order to fit. This will be applied on all cap in cap list,
   *     unless cap specified his own udp_mtu. defaults to None.
   */
  public ASTFProfile(
      ASTFIpGen defaultIpGen,
      ASTFGlobalInfo astfClientGlobalInfo,
      ASTFGlobalInfo astfServerGlobalInfo,
      List<ASTFTemplate> astfTemplateList,
      List<ASTFCapInfo> astfCapInfoList,
      ASTFCmdDelayRnd sDelay,
      Integer udpMtu) {
    createASTFProfile(
        defaultIpGen,
        astfClientGlobalInfo,
        astfServerGlobalInfo,
        astfTemplateList,
        astfCapInfoList,
        sDelay,
        udpMtu);
  }

  private void createASTFProfile(
      ASTFIpGen defaultIpGen,
      ASTFGlobalInfo astfClientGlobalInfo,
      ASTFGlobalInfo astfServerGlobalInfo,
      List<ASTFTemplate> astfTemplateList,
      List<ASTFCapInfo> astfCapInfoList,
      ASTFCmd sDelay,
      Integer udpMtu) {
    if (sDelay != null
        && !(sDelay instanceof ASTFCmdDelay)
        && !(sDelay instanceof ASTFCmdDelayRnd)) {
      throw new IllegalStateException(
          "bad param sDelay, it should be instanceof ASTFCmdDelayRnd or  ASTFCmdDelay");
    }
    this.astfClientGlobalInfo = astfClientGlobalInfo;
    this.astfServerGlobalInfo = astfServerGlobalInfo;
    if (astfTemplateList == null && astfCapInfoList == null) {
      throw new IllegalStateException(
          "bad param combination,ASTFTemplate and ASTFCapInfo should not be null at the same time ");
    }

    if (astfTemplateList != null && !astfTemplateList.isEmpty()) {

      this.astfTemplateList = astfTemplateList;
      List<Integer> serverPorts = new ArrayList<>();
      for (ASTFTemplate template : astfTemplateList) {
        addTgIdToTemplate(template);
        ASTFAssociation association = template.getAstfTcpServerTemplate().getAssociation();
        int port = association.getPort();
        if (association.isPortOnly()) {
          if (serverPorts.contains(port)) {
            throw new IllegalStateException(
                String.format("Two server template with port: %s", port));
          } else {
            serverPorts.add(port);
          }
        }
      }
    }

    /** for pcap file parse scenario */
    if (astfCapInfoList != null && !astfCapInfoList.isEmpty()) {
      String mode = null;
      List<Map<String, Object>> allCapInfo = new ArrayList<>();
      Map<Integer, String> dPorts = new HashMap<>();
      int totalPayload = 0;

      for (ASTFCapInfo capInfo : astfCapInfoList) {
        String capFile = capInfo.getFilePath();
        ASTFIpGen ipGen = capInfo.getAstfIpGen() != null ? capInfo.getAstfIpGen() : defaultIpGen;
        ASTFGlobalInfoPerTemplate globC = capInfo.getClientGlobInfo();
        ASTFGlobalInfoPerTemplate globS = capInfo.getServerGlobInfo();
        Integer capUdpMtu = capInfo.getUdpMtu() != null ? capInfo.getUdpMtu() : udpMtu;
        ASTFProgram programC = new ASTFProgram(capFile, ASTFProgram.SideType.Client, capUdpMtu);
        ASTFCmd serverDelay = capInfo.getsDelay() != null ? capInfo.getsDelay() : sDelay;
        ASTFProgram programS =
            new ASTFProgram(capFile, ASTFProgram.SideType.Server, capUdpMtu, serverDelay);
        programC.updateKeepalive(programS);
        if (!programC.isStream()) {
          programS.updateKeepalive(programC);
        }

        ASTFTCPInfo tcpC = new ASTFTCPInfo(capFile);
        int tcpCPort = tcpC.getPort();

        float caps = capInfo.getCps();
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

        totalPayload += programC.getPayloadLen();

        int dPort;
        ASTFAssociation myAssoc;
        if (capInfo.getAssoc() == null) {
          dPort = tcpCPort;
          myAssoc = new ASTFAssociation(new ASTFAssociationRule(dPort));
        } else {
          myAssoc = capInfo.getAssoc();
          dPort = myAssoc.getPort();
        }

        if (dPorts.containsKey(dPort)) {
          throw new IllegalStateException(
              String.format(
                  "More than one cap use dest port %s. This is currently not supported. Files with same port: %s, %s",
                  dPort, dPorts.get(dPort), capFile));
        }

        dPorts.put(dPort, capFile);
        HashMap<String, Object> map = new HashMap<>();
        map.put("ip_gen", ipGen);
        map.put("prog_c", programC);
        map.put("prog_s", programS);
        map.put("glob_c", globC);
        map.put("glob_s", globS);
        map.put("cps", caps);
        map.put("d_port", dPort);
        map.put("my_assoc", myAssoc);
        map.put("limit", capInfo.getLimit());
        map.put("cont", capInfo.isCont());
        map.put("tg_name", capInfo.getTgName());
        allCapInfo.add(map);
      }

      if (mode.equals(L7_PRECENT)) {
        float percentSum = 0;
        for (Map<String, Object> map : allCapInfo) {
          float newCaps = ((ASTFProgram) map.get("prog_c")).getPayloadLen() * 100.0f / totalPayload;
          map.put(CPS, newCaps);
          percentSum += newCaps;
        }
        if (percentSum != 100) {
          throw new IllegalStateException("l7_percent values must sum up to 100");
        }
      }
      this.astfTemplateList = new ArrayList<ASTFTemplate>();
      for (Map<String, Object> map : allCapInfo) {
        ASTFTCPClientTemplate tempC =
            new ASTFTCPClientTemplate(
                (ASTFProgram) map.get("prog_c"),
                (ASTFIpGen) map.get("ip_gen"),
                null,
                (int) map.get("d_port"),
                (float) map.get("cps"),
                (ASTFGlobalInfoPerTemplate) map.get("glob_c"),
                (int) map.get("limit"),
                (boolean) map.get("cont"));

        ASTFTCPServerTemplate tempS =
            new ASTFTCPServerTemplate(
                (ASTFProgram) map.get("prog_s"),
                (ASTFAssociation) map.get("my_assoc"),
                (ASTFGlobalInfoPerTemplate) map.get("glob_s"));

        ASTFTemplate template = new ASTFTemplate(tempC, tempS, (String) map.get("tg_name"));
        addTgIdToTemplate(template);

        this.astfTemplateList.add(template);
      }
    }
  }

  private void addTgIdToTemplate(ASTFTemplate template) {
    String templateTgName = template.getTgName();
    if (this.tgName2TgId.containsKey(templateTgName)) {
      template.setTgId(this.tgName2TgId.get(templateTgName));
    } else {
      if (StringUtils.isEmpty(templateTgName)) {
        template.setTgId(0);
      } else {
        int id = this.tgName2TgId.size() + 1;
        template.setTgId(id);
        this.tgName2TgId.put(templateTgName, id);
      }
    }
  }

  public String toJsonStr() {
    return toJsonStr(true, false);
  }

  public String toJsonStr(boolean pretty, boolean sortKeys) {
    JsonObject data = toJson();
    GsonBuilder gsonBuilder = new GsonBuilder();
    if (pretty) {
      gsonBuilder.setPrettyPrinting();
    }
    JsonParser parser = new JsonParser();
    JsonElement e = parser.parse(data.toString());
    if (sortKeys) {
      GsonUtil.sort(e);
    }

    return gsonBuilder.create().toJson(e);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    astfProfileCache.fillCache();
    jsonObject.add("buf_list", astfProfileCache.getProgramCache().toJson());
    jsonObject.add("ip_gen_dist_list", astfProfileCache.getGenDistCache().toJson());
    jsonObject.add("program_list", astfProfileCache.getTemplateCache().toJson());
    if (astfClientGlobalInfo != null) {
      jsonObject.add("c_glob_info", astfClientGlobalInfo.toJson());
    }
    if (astfServerGlobalInfo != null) {
      jsonObject.add("s_glob_info", astfServerGlobalInfo.toJson());
    }
    JsonArray jsonArray = new JsonArray();
    if (astfTemplateList != null) {
      for (ASTFTemplate template : astfTemplateList) {
        jsonArray.add(template.toJson());
      }
    }

    jsonObject.add("templates", jsonArray);
    JsonArray tgNames = new JsonArray();
    tgName2TgId.keySet().forEach(name -> tgNames.add(name));
    jsonObject.add("tg_names", tgNames);
    return jsonObject;
  }

  public void printStats() {
    astfProfileCache.fillCache();
    float totalBps = 0;
    float totalCps = 0;

    LOGGER.info("Num buffers: {}", astfProfileCache.getProgramCache().getLen());
    LOGGER.info("Num programs: {}", astfProfileCache.getTemplateCache().getNumPrograms());
    for (int i = 0; i < astfTemplateList.size(); i++) {
      LOGGER.info("------------------------------");
      LOGGER.info("template {}:", i);
      JsonObject tempJson = astfTemplateList.get(i).toJson();
      int cProgIndex = tempJson.getAsJsonObject("client_template").get("program_index").getAsInt();
      int sProgIndex = tempJson.getAsJsonObject("server_template").get("program_index").getAsInt();
      long totalBytes =
          astfProfileCache.getTemplateCache().getTotalSendBytes(cProgIndex)
              + astfProfileCache.getTemplateCache().getTotalSendBytes(sProgIndex);
      float tempCps = tempJson.getAsJsonObject("client_template").get("cps").getAsFloat();
      float tempBps = totalBytes * tempCps * 8;
      LOGGER.info("total bytes:{} cps:{} bps(bytes * cps * 8):{}", totalBytes, tempCps, tempBps);
      totalBps += tempBps;
      totalCps += tempCps;
    }
    LOGGER.info("total for all templates - cps:{} bps:{}", totalCps, totalBps);
  }

  public void clearCache() {
    astfProfileCache.clearAll();
  }

  public List<ASTFTemplate> getAstfTemplateList() {
    return astfTemplateList;
  }
}
