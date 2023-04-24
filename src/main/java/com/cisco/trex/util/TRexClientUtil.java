package com.cisco.trex.util;

import com.cisco.trex.stateless.TRexCommand;
import com.cisco.trex.stateless.TRexTransport;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.model.RPCResponse;
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
    int majorVersion = Constants.ASTF_API_VERSION_MAJOR;
    int minorVersion = Constants.ASTF_API_VERSION_MINOR;
    parameters.put("major", majorVersion);
    parameters.put("minor", minorVersion);
    RPCResponse response = null;
    try {
      TRexCommand command = buildCommand("api_sync_v2", parameters);
      response = transport.sendCommand(command);

      // Currently the TRex server has  the NBC  issue to uplift the  ASTF_API_VERSION_MAJOR
      // version
      // This if-block is a temporary solution to support uplift the  ASTF_API_VERSION_MAJOR version
      // ,
      // if the TRex server does not uplift its version ,the client will continue use the old api,
      // if the server uplift, the client will use the new api.
      String errorMessage =
          response.getError() == null ? null : response.getError().getSpecificErr();
      if (!StringUtils.isBlank(errorMessage) && errorMessage.contains("Version mismatch")) {
        String regrexString = "server: '([0-9]+)\\.([0-9]+)', client: '([0-9]+)\\.([0-9]+)'";
        Pattern pattern = Pattern.compile(regrexString);
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
          majorVersion = Integer.parseInt(matcher.group(1));
          minorVersion = Integer.parseInt(matcher.group(2));
          if (!TRexClientUtil.isVersionCorrect(TRexServerMode.ASTF, majorVersion, minorVersion)) {
            new TRexConnectionException(
                "Unable to connect to TRex server. Required API version is "
                    + majorVersion
                    + "."
                    + minorVersion);
          }
          parameters.put("major", majorVersion);
          parameters.put("minor", minorVersion);

          command = buildCommand("api_sync_v2", parameters);
          response = transport.sendCommand(command);
        }
      }
    } catch (IOException | NullPointerException e) {
      LOGGER.debug("Unable to sync client with TRex server .", e);
      return TRexServerMode.UNKNOWN;
    } finally {
      transport.close();
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

  public static boolean isVersionCorrect(TRexServerMode mode, int major, int minor) {
    switch (mode) {
        // STL mode support only version not small than 4.6
      case STL:
        if (major < 4 || (major == 4 && minor < 6)) {
          return false;
        }
        break;
        // ASTF mode support only version not small than 1.7
      case ASTF:
        if (major < 1 || (major == 1 && minor < 7)) {
          return false;
        }
        break;
    }
    return true;
  }
}
