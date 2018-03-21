package com.cisco.trex.stateless;

import com.cisco.trex.stateless.model.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TRexClientApp {
  public static TRexClient client = null;
  public static final String client_name = "trex-test-user";
  public static final String trex_host = "trex.mycompany.com";
  public static final String trex_port = "4501";
  private static Logger logger = LoggerFactory.getLogger(TRexClientApp.class);

  // API usage example. see also:
  // https://github.com/cisco-system-traffic-generator/trex-java-sdk/blob/master/src/test/java/com/cisco/trex/stateless/TRexClientTest.java

  public static void main(String[] args) {
    client = new TRexClient("tcp", trex_host, trex_port, client_name);
    client.connect();
    List<Port> ports = client.getPorts();
    System.out.println("Found " + ports.size() + " ports");
    List<String> cmds = client.getSupportedCommands();
    logger.info("List of available commands:");
    for (String cmd : cmds) {
      logger.info(" - " + cmd);
    }
    client.disconnect();
  }
}
