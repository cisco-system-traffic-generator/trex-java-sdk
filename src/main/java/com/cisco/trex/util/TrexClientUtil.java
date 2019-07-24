package com.cisco.trex.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.cisco.trex.stateless.TRexCommand;
import com.cisco.trex.stateless.TRexTransport;
import com.cisco.trex.stateless.model.RPCResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class for Trex Client
 */
public final class TrexClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrexClientUtil.class);
    private static final String JSON_RPC_VERSION = "2.0";
    private static final Random randomizer = new Random();
    private static Integer API_VERSION_MAJOR = 1;
    private static Integer API_VERSION_MINOR = 5;
    private static TRexTransport transport;

    private TrexClientUtil() {
    }

    public static TrexServerMode getMode(String host) {
        transport = new TRexTransport(host, "4501", 3000);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", "ASTF");
        parameters.put("major", API_VERSION_MAJOR);
        parameters.put("minor", API_VERSION_MINOR);
        RPCResponse response = null;
        try {
            response = transport.sendCommand(buildCommand("api_sync_v2", parameters));
        } catch (IOException | NullPointerException e) {
            LOGGER.debug("Unable to sync client with TRex server .", e.getMessage());
            return TrexServerMode.UNKNOWN;
        }
        if (!response.isFailed()) {
            return TrexServerMode.ASTF;
        }
        if ("RPC configuration mismatch - server RPC configuration: 'STL', client RPC configuration: 'ASTF'".equalsIgnoreCase(response.getError().getSpecificErr())) {
            return TrexServerMode.STL;
        }
        return TrexServerMode.UNKNOWN;
    }

    /**
     * Build Command
     *
     * @param methodName
     * @param parameters
     * @return TRexCommand
     */
    private static TRexCommand buildCommand(String methodName, Map<String, Object> parameters) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put("api_h", "");
        Map<String, Object> payload = new HashMap<>();
        int cmdId = randomizer.nextInt() & Integer.MAX_VALUE; //get a positive random value
        payload.put("id", cmdId);
        payload.put("jsonrpc", JSON_RPC_VERSION);
        payload.put("method", methodName);
        payload.put("params", parameters);
        return new TRexCommand(cmdId, methodName, payload);
    }
}
