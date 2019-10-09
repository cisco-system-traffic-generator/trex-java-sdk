package com.cisco.trex.stateless;

import static org.junit.Assert.assertTrue;

import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.model.RPCResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@SuppressWarnings("javadoc")
public class TRexClientMethodsTest {

  private static TRexTransport transport = Mockito.mock(TRexTransport.class);
  private static TRexClient client;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final String PORT_STATUS =
      "[{\"id\":1113911190,\"jsonrpc\":\"2.0\",\"result\":{\"attr\":{\"fc\":{\"mode\":0},\"layer_cfg\":{\"ether\":{\"dst\":\"00:00:00:00:00:00\",\"src\":\"08:00:27:1a:58:90\",\"state\":\"unconfigured\"},\"ipv4\":{\"state\":\"none\"},\"ipv6\":{\"enabled\":false}},\"link\":{\"up\":true},\"multicast\":{\"enabled\":false},\"promiscuous\":{\"enabled\":false},\"rx_filter_mode\":\"hw\",\"speed\":1.0,\"vlan\":{\"tags\":[]},\"vxlan_fs\":[]},\"max_stream_id\":0,\"owner\":\"\",\"profile_count\":1,\"rx_info\":{\"capture_port\":{\"is_active\":false},\"capwap_proxy\":{\"is_active\":false},\"grat_arp\":{\"is_active\":false},\"latency\":{\"is_active\":false},\"queue\":{\"is_active\":false},\"stack\":{\"is_active\":true}},\"service\":false,\"state\":\"IDLE\"}}]";
  private static final String[] SET_VALUES =
      new String[] {
        "api_sync_v2",
        "get_supported_cmds",
        "acquire",
        "get_port_status",
        "get_profile_list",
        "get_all_streams",
        "start_traffic",
        "stop_traffic",
        "remove_all_streams",
        "release"
      };
  private static final Set<String> SUPPORTED_COMMANDS = new HashSet<>(Arrays.asList(SET_VALUES));

  @BeforeClass
  public static void setup() {
    client = new TRexClient(transport, SUPPORTED_COMMANDS);
  }

  @Before
  public void beforeTest() {
    Mockito.reset(transport);
  }

  /** Test that TRex client can handle single and array json answer from server */
  @Test
  public void jsonResponsTest() {
    String supportedCmds =
        "{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":[\"push_remote\",\"remove_rx_filters\",\"update_streams\",\"pause_streams\",\"pause_traffic\",\"stop_traffic\",\"service\",\"start_traffic\",\"remove_all_streams\",\"remove_stream\",\"add_stream\",\"resume_traffic\",\"start_capture_port\",\"shutdown\",\"conf_ipv6\",\"get_supported_cmds\",\"get_all_streams\",\"stop_capture_port\",\"get_global_stats\",\"api_sync_v2\",\"get_version\",\"validate\",\"update_traffic\",\"get_stream\",\"get_pgid_stats\",\"set_l2\",\"get_system_info\",\"set_capture_port_bpf\",\"get_profile_list\",\"get_port_status\",\"get_utilization\",\"publish_now\",\"ping\",\"set_vlan\",\"set_port_attr\",\"set_l3\",\"get_port_xstats_names\",\"get_port_xstats_values\",\"api_sync\",\"get_async_results\",\"get_port_stats\",\"get_owner\",\"push_pkts\",\"cancel_async_task\",\"capture\",\"get_rx_queue_pkts\",\"set_rx_feature\",\"acquire\",\"conf_ns_batch\",\"get_active_pgids\",\"release\",\"resume_streams\",\"get_stream_list\"]}";
    String supportedCmds2 =
        "[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":[\"push_remote\",\"remove_rx_filters\",\"update_streams\",\"pause_streams\",\"pause_traffic\",\"stop_traffic\",\"service\",\"start_traffic\",\"remove_all_streams\",\"remove_stream\",\"add_stream\",\"resume_traffic\",\"start_capture_port\",\"shutdown\",\"conf_ipv6\",\"get_supported_cmds\",\"get_all_streams\",\"stop_capture_port\",\"get_global_stats\",\"api_sync_v2\",\"get_version\",\"validate\",\"update_traffic\",\"get_stream\",\"get_pgid_stats\",\"set_l2\",\"get_system_info\",\"set_capture_port_bpf\",\"get_profile_list\",\"get_port_status\",\"get_utilization\",\"publish_now\",\"ping\",\"set_vlan\",\"set_port_attr\",\"set_l3\",\"get_port_xstats_names\",\"get_port_xstats_values\",\"api_sync\",\"get_async_results\",\"get_port_stats\",\"get_owner\",\"push_pkts\",\"cancel_async_task\",\"capture\",\"get_rx_queue_pkts\",\"set_rx_feature\",\"acquire\",\"conf_ns_batch\",\"get_active_pgids\",\"release\",\"resume_streams\",\"get_stream_list\"]}]";
    Mockito.when(transport.sendJson(ArgumentMatchers.anyString())).thenReturn(supportedCmds);

    List<String> commands = client.getSupportedCommands();
    assertTrue("faulty respons", commands.contains("update_streams"));

    Mockito.when(transport.sendJson(ArgumentMatchers.anyString())).thenReturn(supportedCmds2);
    List<String> commands2 = client.getSupportedCommands();
    assertTrue("faulty respons", commands2.contains("update_streams"));
  }

  @Test
  public void serverAPISyncTest()
      throws TRexConnectionException, JsonParseException, JsonMappingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    RPCResponse[] response =
        objectMapper.readValue(
            "[{\"id\":1417504019,\"jsonrpc\":\"2.0\",\"result\":{\"api_h\":\"8jNorAXh\"}}]",
            RPCResponse[].class);
    Mockito.when(transport.sendCommand(ArgumentMatchers.any(TRexCommand.class)))
        .thenReturn(response[0]);

    client.serverAPISync();
  }

  @Test(expected = TRexConnectionException.class)
  public void serverAPISyncExceptionTest()
      throws TRexConnectionException, JsonParseException, JsonMappingException, IOException {
    RPCResponse[] response =
        OBJECT_MAPPER.readValue(
            "[{\"id\":1417504019,\"jsonrpc\":\"2.0\",\"result\":null}]", RPCResponse[].class);
    Mockito.when(transport.sendCommand(ArgumentMatchers.any(TRexCommand.class)))
        .thenReturn(response[0]);

    client.serverAPISync();
  }

  /**
   * JSON Req:
   * {"method":"acquire","id":"aggogxls","jsonrpc":"2.0","params":{"api_h":"Zp76xIYz","port_id":0,"session_id":123456789,"force":true,"user":"trex"}}
   * JSON Resp: [{"id":"aggogxls","jsonrpc":"2.0","result":"UKoNP1hY"}]
   *
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonParseException
   */
  @Test
  public void acquirePortTest() throws JsonParseException, JsonMappingException, IOException {
    RPCResponse[] response = OBJECT_MAPPER.readValue(PORT_STATUS, RPCResponse[].class);
    Mockito.when(transport.sendCommand(ArgumentMatchers.any(TRexCommand.class)))
        .thenReturn(response[0]);
    Mockito.when(transport.sendJson(ArgumentMatchers.contains("\"acquire\"")))
        .thenReturn("[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":\"UKoNP1hY\"}]");

    client.acquirePort(0, true);
  }

  /**
   * JSON Req:
   * {"method":"get_port_status","id":1113911190,"jsonrpc":"2.0","params":{"api_h":"Zp76xIYz","port_id":0,"block":false}}
   * JSON Resp:
   * [{"id":1113911190,"jsonrpc":"2.0","result":{"attr":{"fc":{"mode":0},"layer_cfg":{"ether":{"dst":"00:00:00:00:00:00","src":"08:00:27:1a:58:90","state":"unconfigured"},"ipv4":{"state":"none"},"ipv6":{"enabled":false}},"link":{"up":true},"multicast":{"enabled":false},"promiscuous":{"enabled":false},"rx_filter_mode":"hw","speed":1.0,"vlan":{"tags":[]},"vxlan_fs":[]},"max_stream_id":0,"owner":"","profile_count":1,"rx_info":{"capture_port":{"is_active":false},"capwap_proxy":{"is_active":false},"grat_arp":{"is_active":false},"latency":{"is_active":false},"queue":{"is_active":false},"stack":{"is_active":true}},"service":false,"state":"IDLE"}}]
   *
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonParseException
   */
  @Test
  public void getPortStatusTest() throws JsonParseException, JsonMappingException, IOException {
    RPCResponse[] response = OBJECT_MAPPER.readValue(PORT_STATUS, RPCResponse[].class);
    Mockito.when(transport.sendCommand(ArgumentMatchers.any(TRexCommand.class)))
        .thenReturn(response[0]);

    client.getPortStatus(0);
  }

  /**
   * JSON Req:
   * {"method":"get_profile_list","id":"aggogxls","jsonrpc":"2.0","params":{"handler":"THSLqmTH","api_h":"Zp76xIYz","port_id":1}}
   * JSON Resp: [{"id":"aggogxls","jsonrpc":"2.0","result":["_"]}]
   */
  @Test
  public void getProfileIdsTest() {
    Mockito.when(transport.sendJson(ArgumentMatchers.contains("\"get_profile_list\"")))
        .thenReturn("[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":[\"_\"]}]");

    client.getProfileIds(0);
  }

  /**
   * JSON Req:
   * {"method":"get_all_streams","id":"aggogxls","jsonrpc":"2.0","params":{"handler":"THSLqmTH","profile_id":"_","api_h":"Zp76xIYz","port_id":1}}
   * JSON Resp:
   * [{"id":"aggogxls","jsonrpc":"2.0","result":{"streams":{"131040989":{"action_count":0,"core_id":-1,"enabled":true,"flags":3,"flow_stats":{"enabled":false,"rule_type":"stats","stream_id":131040989},"id":131040989,"isg":0.0,"mode":{"count":0,"ibg":0.0,"pkts_per_burst":0,"rate":{"type":"pps","value":1.0},"total_pkts":0,"type":"continuous"},"next_stream_id":-1,"packet":{"binary":"////////AFBWlCHfCAYAAQgABgQAAQBQVpQh38CoCRv////////AqAkcAAAAAAAAAAAAAAAAAAAAAAAA","meta":""},"random_seed":0,"rx_stats":{"enabled":true,"latency_enabled":true,"seq_enabled":true,"stream_id":131040989},"self_start":true,"vm":{"instructions":[],"split_by_var":""}},"1360153259":{"action_count":0,"core_id":-1,"enabled":true,"flags":3,"flow_stats":{"enabled":false,"rule_type":"stats","stream_id":1360153259},"id":1360153259,"isg":0.0,"mode":{"count":0,"ibg":0.0,"pkts_per_burst":0,"rate":{"type":"pps","value":1.0},"total_pkts":0,"type":"continuous"},"next_stream_id":-1,"packet":{"binary":"////////AFBWlCHfCAYAAQgABgQAAQBQVpQh38CoCRv////////AqAkcAAAAAAAAAAAAAAAAAAAAAAAA","meta":""},"random_seed":0,"rx_stats":{"enabled":true,"latency_enabled":true,"seq_enabled":true,"stream_id":1360153259},"self_start":true,"vm":{"instructions":[],"split_by_var":""}}}}}]
   */
  @Test
  public void getAllStreamsTest() {
    Mockito.when(transport.sendJson(ArgumentMatchers.contains("\"get_all_streams\"")))
        .thenReturn(
            "[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":{\"streams\":{\"131040989\":{\"action_count\":0,\"core_id\":-1,\"enabled\":true,\"flags\":3,\"flow_stats\":{\"enabled\":false,\"rule_type\":\"stats\",\"stream_id\":131040989},\"id\":131040989,\"isg\":0.0,\"mode\":{\"count\":0,\"ibg\":0.0,\"pkts_per_burst\":0,\"rate\":{\"type\":\"pps\",\"value\":1.0},\"total_pkts\":0,\"type\":\"continuous\"},\"next_stream_id\":-1,\"packet\":{\"binary\":\"////////AFBWlCHfCAYAAQgABgQAAQBQVpQh38CoCRv////////AqAkcAAAAAAAAAAAAAAAAAAAAAAAA\",\"meta\":\"\"},\"random_seed\":0,\"rx_stats\":{\"enabled\":true,\"latency_enabled\":true,\"seq_enabled\":true,\"stream_id\":131040989},\"self_start\":true,\"vm\":{\"instructions\":[],\"split_by_var\":\"\"}},\"1360153259\":{\"action_count\":0,\"core_id\":-1,\"enabled\":true,\"flags\":3,\"flow_stats\":{\"enabled\":false,\"rule_type\":\"stats\",\"stream_id\":1360153259},\"id\":1360153259,\"isg\":0.0,\"mode\":{\"count\":0,\"ibg\":0.0,\"pkts_per_burst\":0,\"rate\":{\"type\":\"pps\",\"value\":1.0},\"total_pkts\":0,\"type\":\"continuous\"},\"next_stream_id\":-1,\"packet\":{\"binary\":\"////////AFBWlCHfCAYAAQgABgQAAQBQVpQh38CoCRv////////AqAkcAAAAAAAAAAAAAAAAAAAAAAAA\",\"meta\":\"\"},\"random_seed\":0,\"rx_stats\":{\"enabled\":true,\"latency_enabled\":true,\"seq_enabled\":true,\"stream_id\":1360153259},\"self_start\":true,\"vm\":{\"instructions\":[],\"split_by_var\":\"\"}}}}}]");

    client.getAllStreams(0);
  }

  /**
   * JSON Req:
   * {"method":"start_traffic","id":"aggogxls","jsonrpc":"2.0","params":{"duration":-1.0,"handler":"THSLqmTH","mul":{"op":"abs","type":"raw","value":1.0},"profile_id":"_","api_h":"Zp76xIYz","port_id":1,"core_mask":1,"force":true}}
   * JSON Resp:
   * [{"id":"aggogxls","jsonrpc":"2.0","result":{"multiplier":1.0,"ts":1924.383710379147}}]
   */
  @Test
  public void startAllTraffic() {
    Map<String, Object> mul = new HashMap<>();
    mul.put("op", "abs");
    mul.put("type", "pps");
    mul.put("value", 1.0);
    Mockito.when(transport.sendJson(ArgumentMatchers.contains("\"start_traffic\"")))
        .thenReturn(
            "[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":{\"multiplier\":1.0,\"ts\":1924.383710379147}}]");

    client.startTraffic(0, -1, true, mul, 0);
  }

  /**
   * JSON Req:
   * {"method":"stop_traffic","id":"aggogxls","jsonrpc":"2.0","params":{"handler":"THSLqmTH","profile_id":"_","api_h":"Zp76xIYz","port_id":1}}
   * JSON Resp: [{"id":"aggogxls","jsonrpc":"2.0","result":{}}]
   */
  @Test
  public void stopAllTraffic() {
    Mockito.when(transport.sendJson(ArgumentMatchers.contains("\"get_profile_list\"")))
        .thenReturn("[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":[\"_\"]}]");
    Mockito.when(transport.sendJson(ArgumentMatchers.contains("\"stop_traffic\"")))
        .thenReturn("[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":{}}]");

    client.stopAllTraffic(0);
  }

  /**
   * JSON Req:
   * {"method":"remove_all_streams","id":"aggogxls","jsonrpc":"2.0","params":{"handler":"6rHxHisC","profile_id":"_","api_h":"Zp76xIYz","port_id":1}}
   * JSON Resp: [{"id":"aggogxls","jsonrpc":"2.0","result":{}}]
   */
  @Test
  public void removeAllStreams() {
    Mockito.when(transport.sendJson(ArgumentMatchers.contains("\"remove_all_streams\"")))
        .thenReturn("[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":{}}]");

    client.removeAllStreams(0);
  }

  /**
   * JSON Req:
   * {"method":"release","id":"aggogxls","jsonrpc":"2.0","params":{"handler":"6rHxHisC","api_h":"Zp76xIYz","port_id":1,"user":"trex"}}
   * JSON Resp: [{"id":"aggogxls","jsonrpc":"2.0","result":{}}]
   *
   * @throws IOException
   */
  @Test
  public void releasePort() throws IOException {
    RPCResponse[] response = OBJECT_MAPPER.readValue(PORT_STATUS, RPCResponse[].class);
    Mockito.when(transport.sendCommand(ArgumentMatchers.any(TRexCommand.class)))
        .thenReturn(response[0]);
    Mockito.when(transport.sendJson(ArgumentMatchers.contains("\"release\"")))
        .thenReturn("[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":{}}]");

    client.releasePort(0);
  }
}
