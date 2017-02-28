package com.cisco.trex.stateless;

import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.exception.TRexTimeoutException;
import com.cisco.trex.stateless.model.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TRexClientApp {
    public static TRexClient client = null;
    public static final String client_name = "trex-test-user";
    public static final String trex_host = "trex.mycompany.com";
    public static final String trex_port = "4501";

    // API usage example. see also:
    // https://github.com/cisco-system-traffic-generator/trex-java-sdk/blob/master/src/test/java/com/cisco/trex/stateless/TRexClientTest.java

    public static void main(String[] args) {
        client = new TRexClient("tcp", trex_host, trex_port, client_name);
        client.connect();
        List<Port> ports = client.getPorts();
        System.out.println("Found " + ports.size() + " ports");
        List<String> cmds = client.getSupportedCommands();
        for (String cmd: cmds) {
            System.out.println(" - " + cmd);
        }
        client.disconnect();
    }
}

