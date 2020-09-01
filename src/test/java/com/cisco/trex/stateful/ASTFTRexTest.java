package com.cisco.trex.stateful;

import com.cisco.trex.stateful.api.lowlevel.ASTFProfile;
import com.cisco.trex.stateful.model.ServerStatus.State;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.exception.TRexTimeoutException;
import java.util.concurrent.TimeUnit;
import org.junit.BeforeClass;
import org.junit.Test;

public class ASTFTRexTest {

  public static final String CLIENT_USER = "root";
  static final String HOST = "10.76.176.8";
  static final String RPC_PORT = "4501";
  public static TRexAstfClient client;
  private int fsize = 10;
  private int nflows = 1;
  private int tinc = 0;
  ASTFProfile profile;
  private String profileId;
  private static final String PROFILEID_PREFIX = "astf_profile_";
  protected static final long DEFAULT_CLIENT_MASK = Long.parseLong("ffffffff", 16);
  protected static final double DEFAULT_DURATION = -1.0;
  protected static final int DEFAULT_LATENCY_PPS = 0;
  protected static final int DEFAULT_MULT = 1;

  @BeforeClass
  public static void setUp() throws TRexConnectionException, TRexTimeoutException {
    client = new TRexAstfClient(HOST, RPC_PORT, CLIENT_USER);
    client.connect();
    client.acquirePorts(true);
  }

  @Test
  public void testAstfFtpSim() throws InterruptedException {
    profile = AstfFtpSim.createProfile(fsize, nflows, tinc, "defaultName");
    profileId = PROFILEID_PREFIX + System.currentTimeMillis();
    System.out.print("testAstfFtpSim PROFILE:" + profile.toJson());
    profile.clearCache();
    State status = client.syncWithServer().getState();

    System.out.println(
        String.format(
            "Overal traffic state before startTraffic: %s", client.syncWithServer().getState()));

    client.loadProfile(profileId, profile.toJson().toString());
    client.startTraffic(
        profileId,
        DEFAULT_CLIENT_MASK,
        DEFAULT_DURATION,
        false,
        DEFAULT_LATENCY_PPS,
        DEFAULT_MULT,
        false);

    System.out.println(
        String.format(
            "Traffic state after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    // send for 10 seconds
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after  a while after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    client.stopTraffic();
    // wait for 10 seconds for stopping procedure
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after stopTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after stopTraffic: %s", client.syncWithServer().getState()));
  }

  @Test
  public void testFtpMultipleFlowsSim() throws InterruptedException {
    profile = FtpMultipleFlowsSim.createProfile(fsize, nflows, tinc, "defaultName");
    profileId = PROFILEID_PREFIX + System.currentTimeMillis();
    System.out.print("testFtpMultipleFlowsSim PROFILE:" + profile.toJson());
    profile.clearCache();
    State status = client.syncWithServer().getState();

    System.out.println(
        String.format(
            "Overal traffic state before startTraffic: %s", client.syncWithServer().getState()));

    client.loadProfile(profileId, profile.toJson().toString());
    client.startTraffic(
        profileId,
        DEFAULT_CLIENT_MASK,
        DEFAULT_DURATION,
        false,
        DEFAULT_LATENCY_PPS,
        DEFAULT_MULT,
        false);

    System.out.println(
        String.format(
            "Traffic state after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    // send for 10 seconds
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after  a while after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    client.stopTraffic();
    // wait for 10 seconds for stopping procedure
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after stopTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after stopTraffic: %s", client.syncWithServer().getState()));
  }

  @Test
  public void testHttpSimple() throws InterruptedException {
    profile = HttpSimple.getProfile();
    profileId = PROFILEID_PREFIX + System.currentTimeMillis();
    System.out.print("testHttpSimple PROFILE:" + profile.toJson());
    profile.clearCache();
    State status = client.syncWithServer().getState();

    System.out.println(
        String.format(
            "Overal traffic state before startTraffic: %s", client.syncWithServer().getState()));

    client.loadProfile(profileId, profile.toJson().toString());
    client.startTraffic(
        profileId,
        DEFAULT_CLIENT_MASK,
        DEFAULT_DURATION,
        false,
        DEFAULT_LATENCY_PPS,
        DEFAULT_MULT,
        false);

    System.out.println(
        String.format(
            "Traffic state after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    // send for 10 seconds
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after  a while after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    client.stopTraffic();
    // wait for 10 seconds for stopping procedure
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after stopTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after stopTraffic: %s", client.syncWithServer().getState()));
  }

  @Test
  public void testMixPcapFileAstfTraffic() throws InterruptedException {
    profile = MixPcapFileAstfTraffic.createProfile();
    profileId = PROFILEID_PREFIX + System.currentTimeMillis();
    System.out.print("testMixPcapFileAstfTraffic PROFILE:" + profile.toJson());
    profile.clearCache();
    State status = client.syncWithServer().getState();

    System.out.println(
        String.format(
            "Overal traffic state before startTraffic: %s", client.syncWithServer().getState()));

    client.loadProfile(profileId, profile.toJson().toString());
    client.startTraffic(
        profileId,
        DEFAULT_CLIENT_MASK,
        DEFAULT_DURATION,
        false,
        DEFAULT_LATENCY_PPS,
        DEFAULT_MULT,
        false);

    System.out.println(
        String.format(
            "Traffic state after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    // send for 10 seconds
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after  a while after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    client.stopTraffic();
    // wait for 10 seconds for stopping procedure
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after stopTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after stopTraffic: %s", client.syncWithServer().getState()));
  }

  @Test
  public void testParamTcpDelayAck() throws InterruptedException {
    profile = ParamTcpDelayAck.createProfile();
    profileId = PROFILEID_PREFIX + System.currentTimeMillis();
    System.out.print("testParamTcpDelayAck PROFILE:" + profile.toJson());
    profile.clearCache();
    State status = client.syncWithServer().getState();

    System.out.println(
        String.format(
            "Overal traffic state before startTraffic: %s", client.syncWithServer().getState()));

    client.loadProfile(profileId, profile.toJson().toString());
    client.startTraffic(
        profileId,
        DEFAULT_CLIENT_MASK,
        DEFAULT_DURATION,
        false,
        DEFAULT_LATENCY_PPS,
        DEFAULT_MULT,
        false);

    System.out.println(
        String.format(
            "Traffic state after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    // send for 10 seconds
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after  a while after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    client.stopTraffic();
    // wait for 10 seconds for stopping procedure
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after stopTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after stopTraffic: %s", client.syncWithServer().getState()));
  }

  @Test
  public void testParamTcpKeepalive() throws InterruptedException {
    profile = ParamTcpKeepalive.createProfile();
    profileId = PROFILEID_PREFIX + System.currentTimeMillis();
    System.out.print("testParamTcpKeepalive PROFILE:" + profile.toJson());
    profile.clearCache();
    State status = client.syncWithServer().getState();

    System.out.println(
        String.format(
            "Overal traffic state before startTraffic: %s", client.syncWithServer().getState()));

    client.loadProfile(profileId, profile.toJson().toString());
    client.startTraffic(
        profileId,
        DEFAULT_CLIENT_MASK,
        DEFAULT_DURATION,
        false,
        DEFAULT_LATENCY_PPS,
        DEFAULT_MULT,
        false);

    System.out.println(
        String.format(
            "Traffic state after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    // send for 10 seconds
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after  a while after startTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after startTraffic: %s", client.syncWithServer().getState()));

    client.stopTraffic();
    // wait for 10 seconds for stopping procedure
    TimeUnit.SECONDS.sleep(10);
    System.out.println(
        String.format(
            "Traffic state after stopTraffic: %s",
            client.syncWithServer().getStateProfile().get(profileId)));
    System.out.println(
        String.format(
            "Overal traffic state after stopTraffic: %s", client.syncWithServer().getState()));
  }
}
