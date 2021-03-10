package com.cisco.trex.util;

import com.cisco.trex.stateless.TRexCommand;
import com.cisco.trex.stateless.TRexTransport;
import com.cisco.trex.stateless.model.RPCResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Util class for Trex Client */
public final class TRexClientUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(TRexClientUtil.class);
  private static final Random randomizer = new Random();

  private TRexClientUtil() {}

  public static TRexServerMode getMode(String host, String port) {
    TRexTransport transport = new TRexTransport(host, port, 3000);
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("name", "ASTF");
    parameters.put("major", Constants.ASTF_API_VERSION_MAJOR);
    parameters.put("minor", Constants.ASTF_API_VERSION_MINOR);
    RPCResponse response = null;
    try {
      TRexCommand command = buildCommand("api_sync_v2", parameters);
      response = transport.sendCommand(command);

      String errorMessage =
          response.getError() == null ? null : response.getError().getSpecificErr();
      if (!StringUtils.isBlank(errorMessage) && errorMessage.contains("Version mismatch")) {
        String regrexString = "server: '([0-9]*)\\.([0-9]*)', client: '([0-9]*)\\.([0-9]*)'";
        Pattern pattern = Pattern.compile(regrexString);
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
          Constants.ASTF_API_VERSION_MAJOR = Integer.parseInt(matcher.group(1));
          Constants.ASTF_API_VERSION_MINOR = Integer.parseInt(matcher.group(2));
          parameters.put("major", Constants.ASTF_API_VERSION_MAJOR);
          parameters.put("minor", Constants.ASTF_API_VERSION_MINOR);
          command = buildCommand("api_sync_v2", parameters);
          response = transport.sendCommand(command);
        }
      }
    } catch (IOException | NullPointerException e) {
      LOGGER.debug("Unable to sync client with TRex server .", e);
      return TRexServerMode.UNKNOWN;
    }
    if (!response.isFailed()) {
      return TRexServerMode.ASTF;
    }
    if (Pattern.compile(".*mismatch.*server RPC.*STL.")
        .matcher(response.getError().getSpecificErr())
        .find()) {
      return TRexServerMode.STL;
    }
    return TRexServerMode.UNKNOWN;
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
    int cmdId = randomizer.nextInt() & Integer.MAX_VALUE; // get a positive random value
    payload.put("id", cmdId);
    payload.put("jsonrpc", Constants.JSON_RPC_VERSION);
    payload.put("method", methodName);
    payload.put("params", parameters);
    return new TRexCommand(cmdId, methodName, payload);
  }
}
