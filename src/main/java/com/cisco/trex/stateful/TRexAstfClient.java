package com.cisco.trex.stateful;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.cisco.trex.util.Constants;
import org.apache.commons.lang.StringUtils;

import com.cisco.trex.ClientBase;
import com.cisco.trex.stateful.model.stats.AstfStatistics;
import com.cisco.trex.stateful.model.stats.MetaData;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.model.ApiVersionHandler;
import com.cisco.trex.stateless.model.PortStatus;
import com.cisco.trex.stateless.model.TRexClientResult;
import com.cisco.trex.stateless.model.stats.ExtendedPortStatistics;
import com.cisco.trex.stateless.model.stats.XstatsNames;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * TRex Astf Client class
 */
public class TRexAstfClient extends ClientBase {

    private static final String ASTF = "ASTF";

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
        apiVers.put("major", Constants.API_VERSION_MAJOR);
        apiVers.put("minor", Constants.ASTF_API_VERSION_MINOR);
        apiVers.put("name", ASTF);
        TRexClientResult<ApiVersionHandler> result = callMethod("api_sync_v2", apiVers, ApiVersionHandler.class);
        if (result.get() == null) {
            TRexConnectionException e = new TRexConnectionException(
                    "Unable to connect to TRex server. Required API version is " + Constants.API_VERSION_MAJOR + "."
                            + Constants.ASTF_API_VERSION_MINOR);
            LOGGER.error("Unable to sync client with TRex server due to: API_H is null.", e.getMessage());
            throw e;
        }
        this.apiH = result.get().getApiH();
        LOGGER.info("Received api_H: {}", apiH);
    }

    private Map<String, Object> createPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("api_h", apiH);
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
    public void startTraffic(long clientMask, double duration, boolean ipv6,
            int latencyPps, int mult, boolean nc) {
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
    public void startTraffic(String profileId, long clientMask, double duration, boolean ipv6,
            int latencyPps, int mult, boolean nc) {
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
    public void startLatencyTraffic(long mask, int mult, String srcAddr, String dstAddr, String dualPortAddr) {
        Map<String, Object> payload = createPayload();
        payload.put("mask", mask);
        payload.put("mult", mult);
        payload.put("src_addr", srcAddr);
        payload.put("dst_addr", dstAddr);
        payload.put("dual_port_addr", dualPortAddr);
        this.callMethod("start_latency", payload);
    }

    /**
     * Stop the active traffic associated with default profile id
     */
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

    /**
     * Stop all active traffic
     */
    public void stopAllTraffic() {
        List<String> profileIds = getProfileIds();
        for (String profileId : profileIds) {
            stopTraffic(profileId);
        }
    }

    /**
     * Stop active latency traffic
     */
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
     * Acquire Port
     *
     * @param force
     * @param portIndex
     */
    @Override
    public PortStatus acquirePort(int portIndex, Boolean force) {
        Map<String, Object> payload = createPayload();
        payload.put("user", userName);
        payload.put("force", force);
        payload.put("port_id", portIndex);
        String json = callMethod("acquire", payload);
        JsonElement response = new JsonParser().parse(json);
        Set<Entry<String, JsonElement>> entrySet;
        try {
            this.masterHandler = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonObject()
                    .get("handler").getAsString();
            entrySet = response.getAsJsonArray().get(0).getAsJsonObject().get("result")
                    .getAsJsonObject()
                    .get("ports").getAsJsonObject().entrySet();
        } catch (NullPointerException e) {
            throw new IllegalStateException("could not parse attribute, please release the port first", e);
        }
        if (force) {
            portHandlers.clear();
        }
        for (Entry<String, JsonElement> entry : entrySet) {
            portHandlers.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsString());
        }
        LOGGER.info("portHandlers is: {} ", portHandlers);
        return getPortStatus(portIndex).get();
    }

    /**
     * Load profile object as string and upload in fragments
     *
     * @param profile
     */
    public void loadProfile(String profile) {
        loadProfile(profile, "");
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
        int fragmentLength = 1000; //shorter length the first time
        int totalLength = profile.length();
        while (totalLength > indexStart) {
            int indexEnd = indexStart + fragmentLength;
            Map<String, Object> payload = createPayload(profileId);
            if (indexStart == 0) { //is first fragment
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
            fragmentLength = 500000; //larger fragments after first fragment
        }
    }

    /**
     * clear profile on loaded state for default profile id
     */
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
        Map<String, Object> payload = createPayload();
        String json = callMethod("get_profile_list", payload);
        JsonElement response = new JsonParser().parse(json);
        JsonArray ids = response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonArray();
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
        return callMethod("get_counter_values", payload, AstfStatistics.class).get()
                .setCounterNames(getAstfStatsMetaData());
    }

    /**
     * Get ASTF total counters for all profiles
     * 
     * @return AstfStatistics
     */
    public AstfStatistics getAstfTotalStatistics() {
        Map<String, Object> payload = createPayload();
        return callMethod("get_total_counter_values", payload, AstfStatistics.class).get()
                .setCounterNames(getAstfStatsMetaData());
    }

    private MetaData getAstfStatsMetaData() {
        Map<String, Object> payload = createPayload();
        return callMethod("get_counter_desc", payload, MetaData.class).get();
    }

    /**
     * Get Latency Stats
     * Not finished, needs to return counter object
     */
    public void getLatencyStats() {
        Map<String, Object> payload = this.createPayload();
        this.callMethod("get_latency_stats", payload);
    }

    /**
     * Get Version
     *
     * @return version
     */
    public String getVersion() {
        Map<String, Object> payload = this.createPayload();
        String json = callMethod("get_version", payload);
        JsonElement response = new JsonParser().parse(json);
        try {
            return response.getAsJsonArray().get(0).getAsJsonObject().get("result").getAsJsonObject()
                    .get("version").getAsString();
        } catch (NullPointerException e) {
            throw new IllegalStateException("could not parse version", e);
        }
    }

}
