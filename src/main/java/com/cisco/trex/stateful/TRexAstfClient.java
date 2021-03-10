package com.cisco.trex.stateful;

import com.cisco.trex.ClientBase;
import com.cisco.trex.stateful.model.ServerStatus;
import com.cisco.trex.stateful.model.stats.AstfStatistics;
import com.cisco.trex.stateful.model.stats.LatencyPortData;
import com.cisco.trex.stateful.model.stats.LatencyStats;
import com.cisco.trex.stateful.model.stats.MetaData;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.model.ApiVersionHandler;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

/** TRex Astf Client class */
public class TRexAstfClient extends ClientBase {

  private static final String ASTF = "ASTF";
  private MetaData counterMetaData;

  /**
   * @param host
   * @param port
   * @param userName
   */
  public TRexAstfClient(String host, String port, String userName) {
    this.host = host;
    this.port = port;
    this.userName = userName;
    this.supportedCmds.add("api_sync_v2");
    this.supportedCmds.add("get_supported_cmds");
  }

  @Override
  protected void serverAPISync() throws TRexConnectionException {
    LOGGER.info("Sync API with the TRex");
    Map<String, Object> apiVers = new HashMap<>();
    apiVers.put("major", Constants.ASTF_API_VERSION_MAJOR);
    apiVers.put("minor", Constants.ASTF_API_VERSION_MINOR);
    apiVers.put("name", ASTF);
    TRexClientResult<ApiVersionHandler> result =
        callMethod("api_sync_v2", apiVers, ApiVersionHandler.class);

    if (!StringUtils.isBlank(result.getError()) && result.getError().contains("Version mismatch")) {
      String regrexString = "server: '([0-9]*)\\.([0-9]*)', client: '([0-9]*)\\.([0-9]*)'";
      Pattern pattern = Pattern.compile(regrexString);
      Matcher matcher = pattern.matcher(result.getError());
      if (matcher.find()) {
        Constants.ASTF_API_VERSION_MAJOR = Integer.parseInt(matcher.group(1));
        Constants.ASTF_API_VERSION_MINOR = Integer.parseInt(matcher.group(2));
        apiVers.put("major", Constants.ASTF_API_VERSION_MAJOR);
        apiVers.put("minor", Constants.ASTF_API_VERSION_MINOR);
        result = callMethod("api_sync_v2", apiVers, ApiVersionHandler.class);
      }
    }

    if (result.get() == null) {
      TRexConnectionException e =
          new TRexConnectionException(
              "Unable to connect to TRex server. Required API version is "
                  + Constants.ASTF_API_VERSION_MAJOR
                  + "."
                  + Constants.ASTF_API_VERSION_MINOR);
      LOGGER.error("Unable to sync client with TRex server due to: API_H is null.", e.getMessage());
      throw e;
    }
    this.apiH = result.get().getApiH();
    LOGGER.info("Received api_H: {}", apiH);
  }

  private Map<String, Object> createPayload() {
    Map<String, Object> payload = new HashMap<>();
    payload.put(API_H, apiH);
    if (!StringUtils.isEmpty(masterHandler)) {
      payload.put("handler", masterHandler);
    }
    return payload;
  }

  protected Map<String, Object> createPayload(String profileId) {
    Map<String, Object> payload = createPayload();
    if (profileId != null && !profileId.isEmpty()) {
      payload.put("profile_id", profileId);
    }
    return payload;
  }

  private static String calculateMd5(String profile) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hashInBytes = md.digest(profile.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : hashInBytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Could not generate MD5", e);
    }
  }

  /**
   * start traffic on all ports on loaded profile associated with default profile id
   *
   * @param clientMask
   * @param duration
   * @param ipv6
   * @param latencyPps
   * @param mult
   * @param nc
   */
  public void startTraffic(
      long clientMask, double duration, boolean ipv6, int latencyPps, int mult, boolean nc) {
    startTraffic("", clientMask, duration, ipv6, latencyPps, mult, nc);
  }

  /**
   * start traffic on all ports on loaded profile associated with specified profile id
   *
   * @param profileId
   * @param clientMask
   * @param duration
   * @param ipv6
   * @param latencyPps
   * @param mult
   * @param nc
   */
  public void startTraffic(
      String profileId,
      long clientMask,
      double duration,
      boolean ipv6,
      int latencyPps,
      int mult,
      boolean nc) {
    Map<String, Object> payload = createPayload(profileId);
    payload.put("client_mask", clientMask);
    payload.put("duration", duration);
    payload.put("ipv6", ipv6);
    payload.put("latency_pps", latencyPps);
    payload.put("mult", mult);
    payload.put("nc", nc);
    this.callMethod("start", payload);
  }

  /**
   * start latency ICMP streams in Rx core
   *
   * @param mask
   * @param mult
   * @param srcAddr
   * @param dstAddr
   * @param dualPortAddr
   */
  public void startLatencyTraffic(
      long mask, int mult, String srcAddr, String dstAddr, String dualPortAddr) {
    Map<String, Object> payload = createPayload();
    payload.put("mask", mask);
    payload.put("mult", mult);
    payload.put("src_addr", srcAddr);
    payload.put("dst_addr", dstAddr);
    payload.put("dual_port_addr", dualPortAddr);
    this.callMethod("start_latency", payload);
  }

  /** Stop the active traffic associated with default profile id */
  public void stopTraffic() {
    stopTraffic("");
  }

  /**
   * Stop the active traffic associated with specified profile id
   *
   * @param profileId
   */
  public void stopTraffic(String profileId) {
    Map<String, Object> payload = createPayload(profileId);
    this.callMethod("stop", payload);
  }

  /** Stop all active traffic */
  public void stopAllTraffic() {
    List<String> profileIds = getProfileIds();
    for (String profileId : profileIds) {
      stopTraffic(profileId);
    }
  }

  /** Stop active latency traffic */
  public void stopLatencyTraffic() {
    Map<String, Object> payload = createPayload();
    this.callMethod("stop_latency", payload);
  }

  /**
   * Update the multiplier of running traffic
   *
   * @param mult
   */
  public void updateTrafficRate(int mult) {
    Map<String, Object> payload = createPayload();
    payload.put("mult", mult);
    this.callMethod("update", payload);
  }

  /**
   * Update the multiplier of running traffic
   *
   * @param mult
   */
  public void updateLatencyTrafficRate(int mult) {
    Map<String, Object> payload = createPayload();
    payload.put("mult", mult);
    this.callMethod("update_latency", payload);
  }

  /**
   * In ASTF mode all ports will be acquired in a single call, not support to acquire a single port
   *
   * @param force
   */
  public void acquirePorts(Boolean force) {
    Map<String, Object> payload = createPayload();
    payload.put("session_id", new Random().nextInt(Integer.MAX_VALUE - 1) + 1);
    payload.put("user", userName);
    payload.put("force", force);
    String json = callMethod("acquire", payload);
    Set<Entry<String, JsonElement>> entrySet;
    try {
      this.masterHandler =
          getResultFromResponse(json).getAsJsonObject().get("handler").getAsString();
      entrySet =
          getResultFromResponse(json).getAsJsonObject().get("ports").getAsJsonObject().entrySet();
    } catch (NullPointerException e) {
      throw new IllegalStateException(
          "could not parse attribute, please release the port first", e);
    }
    if (force) {
      portHandlers.clear();
    }
    for (Entry<String, JsonElement> entry : entrySet) {
      portHandlers.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsString());
    }
    LOGGER.info("portHandlers is: {} ", portHandlers);
  }

  /** Release Ports */
  public void releasePorts() {
    if (StringUtils.isEmpty(masterHandler)) {
      LOGGER.debug("No handler assigned, ports are not acquired.");
    } else {
      Map<String, Object> payload = createPayload();
      payload.put("user", userName);
      String result = callMethod("release", payload);
      if (result.contains("must acquire the context")) {
        LOGGER.info("Ports are not owned by this session, already released or never acquired");
      }
      portHandlers.clear();
    }
  }

  /**
   * Load profile object as string and upload in fragments
   *
   * @param profile
   */
  public void loadProfile(String profile) {
    loadProfile("", profile);
  }

  /**
   * Load profile object as string and upload in fragments and associate it with specified profile
   * id
   *
   * @param profile
   * @param profileId
   */
  public void loadProfile(String profileId, String profile) {
    int indexStart = 0;
    int fragmentLength = 1000; // shorter length the first time
    int totalLength = profile.length();
    while (totalLength > indexStart) {
      int indexEnd = indexStart + fragmentLength;
      Map<String, Object> payload = createPayload(profileId);
      if (indexStart == 0) { // is first fragment
        payload.put("frag_first", true);
        payload.put("total_size", totalLength);
        payload.put("md5", calculateMd5(profile));
      }
      if (indexEnd >= totalLength) {
        payload.put("frag_last", true);
        indexEnd = totalLength;
      }
      payload.put("fragment", profile.subSequence(indexStart, indexEnd));
      this.callMethod("profile_fragment", payload);
      indexStart = indexEnd;
      fragmentLength = 500000; // larger fragments after first fragment
    }
  }

  /** clear profile on loaded state for default profile id */
  public void clearProfile() {
    clearProfile("");
  }

  /**
   * clear profile on loaded state for specified profile id
   *
   * @param profileId
   */
  public void clearProfile(String profileId) {
    Map<String, Object> payload = createPayload(profileId);
    this.callMethod("profile_clear", payload);
  }

  /**
   * fetch all the associated profile ids
   *
   * @return profile id list
   */
  public List<String> getProfileIds() {
    if (StringUtils.isEmpty(masterHandler)) {
      return Collections.emptyList();
    }
    Map<String, Object> payload = createPayload();
    String json = callMethod("get_profile_list", payload);
    JsonArray ids = getResultFromResponse(json).getAsJsonArray();
    return StreamSupport.stream(ids.spliterator(), false)
        .map(JsonElement::getAsString)
        .collect(Collectors.toList());
  }

  /**
   * Get ASTF counters of profile associated with specified profile id
   *
   * @param profileId
   * @return AstfStatistics
   */
  public AstfStatistics getAstfStatistics(String profileId) {
    Map<String, Object> payload = createPayload(profileId);
    return callMethod("get_counter_values", payload, AstfStatistics.class)
        .get()
        .setCounterNames(getAstfStatsMetaData());
  }

  /**
   * Get ASTF total counters for all profiles
   *
   * @return AstfStatistics
   */
  public AstfStatistics getAstfTotalStatistics() {
    Map<String, Object> payload = createPayload();
    return callMethod("get_total_counter_values", payload, AstfStatistics.class)
        .get()
        .setCounterNames(getAstfStatsMetaData());
  }

  private MetaData getAstfStatsMetaData() {
    if (counterMetaData == null) {
      Map<String, Object> payload = createPayload();
      counterMetaData = callMethod("get_counter_desc", payload, MetaData.class).get();
    }

    return counterMetaData;
  }

  /**
   * Get Latency Stats
   *
   * @return LatencyStats
   */
  public LatencyStats getLatencyStats() {
    Map<String, Object> payload = this.createPayload();
    String json = this.callMethod("get_latency_stats", payload);
    JsonElement latencyStatsJsonElement = getResultFromResponse(json);
    // only can parse a part of data, LatencyPortData need to be parsed manually.
    LatencyStats latencyStats = GSON.fromJson(latencyStatsJsonElement, LatencyStats.class);
    JsonElement latencyDataJsonElement = latencyStatsJsonElement.getAsJsonObject().get("data");
    JsonObject latencyDataJsonObject = latencyDataJsonElement.getAsJsonObject();
    Map<Integer, LatencyPortData> portLatencyDataMap = new HashMap<>();
    // parse LatencyPortData manually
    for (Map.Entry<String, JsonElement> entry : latencyDataJsonObject.entrySet()) {
      String jsonKey = entry.getKey();
      if (jsonKey.startsWith("port")) {
        Integer portIndex = Integer.parseInt(jsonKey.substring(5));
        LatencyPortData latencyPortData = GSON.fromJson(entry.getValue(), LatencyPortData.class);
        portLatencyDataMap.put(portIndex, latencyPortData);
      }
    }
    latencyStats.getData().setPortLatencyDataMap(portLatencyDataMap);
    return latencyStats;
  }

  /**
   * Get Version
   *
   * @return version
   */
  public String getVersion() {
    Map<String, Object> payload = this.createPayload();
    String json = callMethod("get_version", payload);
    try {
      return getResultFromResponse(json).getAsJsonObject().get("version").getAsString();
    } catch (NullPointerException e) {
      throw new IllegalStateException("could not parse version", e);
    }
  }

  /**
   * get template group names
   *
   * @return template group names
   */
  public List<String> getTemplateGroupNames() {
    return this.getTemplateGroupNames("");
  }

  /**
   * get template group names
   *
   * @param profileId
   * @return template group names
   */
  public List<String> getTemplateGroupNames(String profileId) {
    if (profileId == null || !getProfileIds().contains(profileId)) {
      LOGGER.warn(
          "can not fetch template group names due to invalid profileId, or relative profile is not loaded yet.");
      return Collections.emptyList();
    }

    Map<String, Object> payload = createPayload(profileId);
    payload.put("initialized", false);
    String json = callMethod("get_tg_names", payload);
    JsonArray names =
        getResultFromResponse(json).getAsJsonObject().get("tg_names").getAsJsonArray();
    return StreamSupport.stream(names.spliterator(), false)
        .map(JsonElement::getAsString)
        .collect(Collectors.toList());
  }

  /**
   * get template group statistics
   *
   * @param tgNames
   * @return group statistics
   */
  public Map<String, AstfStatistics> getTemplateGroupStatistics(List<String> tgNames) {
    return getTemplateGroupStatistics("", tgNames);
  }

  /**
   * get template group statistics
   *
   * @param profileId
   * @param tgNames
   * @return Map key:tgName, value:AstfStatistics
   */
  public Map<String, AstfStatistics> getTemplateGroupStatistics(
      String profileId, List<String> tgNames) {

    // remove duplicated tgNames in input list
    List<String> tgNames2 = new ArrayList<>(new HashSet<>(tgNames));
    Map<String, AstfStatistics> stats = new LinkedHashMap<>(tgNames2.size());

    Map<String, Object> payload = createPayload(profileId);
    payload.put("epoch", 1);
    Map<String, Integer> name2Id = translateNames2Ids(profileId, tgNames2);
    payload.put("tg_ids", new ArrayList<>(name2Id.values()));

    String json = callMethod("get_tg_id_stats", payload);
    JsonObject result = getResultFromResponse(json).getAsJsonObject();
    MetaData metaData = getAstfStatsMetaData();
    name2Id.forEach(
        (tgName, tgId) -> {
          if (result.get(tgId.toString()) == null) {
            return;
          }
          try {
            AstfStatistics astfStatistics =
                new ObjectMapper()
                    .readValue(result.get(tgId.toString()).toString(), AstfStatistics.class);
            astfStatistics.setCounterNames(metaData);
            stats.put(tgName, astfStatistics);
          } catch (IOException e) {
            LOGGER.error("Error occurred during processing output of get_tg_id_stats method", e);
          }
        });

    return stats;
  }

  /**
   * get template group statistics
   *
   * @return Map key:tgName, value:AstfStatistics
   */
  public ServerStatus syncWithServer() {
    Map<String, Object> payload = createPayload("*");
    return callMethod("sync", payload, ServerStatus.class).get();
  }

  /**
   * translate template group names to ids getTemplateGroupNames with return all template group
   * names, this method will map an id for each name, the id is an increasing integer starting at 1.
   * and filter names by input name list
   *
   * @param profileId
   * @param tgNames
   * @return Map key:tgName, value:tgId
   */
  private Map<String, Integer> translateNames2Ids(String profileId, List<String> tgNames) {
    Map<String, Integer> name2Id = new LinkedHashMap<>(tgNames.size());
    List<String> allTgNames = getTemplateGroupNames(profileId);
    for (int i = 0; i < allTgNames.size(); i++) {
      if (tgNames.contains(allTgNames.get(i))) {
        name2Id.put(allTgNames.get(i), i + 1);
      }
    }

    return name2Id;
  }
}
