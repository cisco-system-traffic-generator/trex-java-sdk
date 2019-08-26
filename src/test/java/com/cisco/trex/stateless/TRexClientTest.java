package com.cisco.trex.stateless;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@SuppressWarnings("javadoc")
public class TRexClientTest {

    TRexTransport transport = Mockito.mock(TRexTransport.class);
    TRexClient client;

    private static final String[] SET_VALUES = new String[] { "api_sync_v2", "get_supported_cmds" };
    private static final Set<String> SUPPORTED_COMMANDS = new HashSet<>(Arrays.asList(SET_VALUES));

    @Before
    public void setup() {
        client = new TRexClient(transport, SUPPORTED_COMMANDS);
    }

    @Test
    public void jsonResponsTest() {
        String SUPPORTED_CMDS = "{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":[\"push_remote\",\"remove_rx_filters\",\"update_streams\",\"pause_streams\",\"pause_traffic\",\"stop_traffic\",\"service\",\"start_traffic\",\"remove_all_streams\",\"remove_stream\",\"add_stream\",\"resume_traffic\",\"start_capture_port\",\"shutdown\",\"conf_ipv6\",\"get_supported_cmds\",\"get_all_streams\",\"stop_capture_port\",\"get_global_stats\",\"api_sync_v2\",\"get_version\",\"validate\",\"update_traffic\",\"get_stream\",\"get_pgid_stats\",\"set_l2\",\"get_system_info\",\"set_capture_port_bpf\",\"get_profile_list\",\"get_port_status\",\"get_utilization\",\"publish_now\",\"ping\",\"set_vlan\",\"set_port_attr\",\"set_l3\",\"get_port_xstats_names\",\"get_port_xstats_values\",\"api_sync\",\"get_async_results\",\"get_port_stats\",\"get_owner\",\"push_pkts\",\"cancel_async_task\",\"capture\",\"get_rx_queue_pkts\",\"set_rx_feature\",\"acquire\",\"conf_ns_batch\",\"get_active_pgids\",\"release\",\"resume_streams\",\"get_stream_list\"]}";
        String SUPPORTED_CMDS2 = "[{\"id\":\"aggogxls\",\"jsonrpc\":\"2.0\",\"result\":[\"push_remote\",\"remove_rx_filters\",\"update_streams\",\"pause_streams\",\"pause_traffic\",\"stop_traffic\",\"service\",\"start_traffic\",\"remove_all_streams\",\"remove_stream\",\"add_stream\",\"resume_traffic\",\"start_capture_port\",\"shutdown\",\"conf_ipv6\",\"get_supported_cmds\",\"get_all_streams\",\"stop_capture_port\",\"get_global_stats\",\"api_sync_v2\",\"get_version\",\"validate\",\"update_traffic\",\"get_stream\",\"get_pgid_stats\",\"set_l2\",\"get_system_info\",\"set_capture_port_bpf\",\"get_profile_list\",\"get_port_status\",\"get_utilization\",\"publish_now\",\"ping\",\"set_vlan\",\"set_port_attr\",\"set_l3\",\"get_port_xstats_names\",\"get_port_xstats_values\",\"api_sync\",\"get_async_results\",\"get_port_stats\",\"get_owner\",\"push_pkts\",\"cancel_async_task\",\"capture\",\"get_rx_queue_pkts\",\"set_rx_feature\",\"acquire\",\"conf_ns_batch\",\"get_active_pgids\",\"release\",\"resume_streams\",\"get_stream_list\"]}]";
        Mockito.when(transport.sendJson(ArgumentMatchers.anyString())).thenReturn(SUPPORTED_CMDS);

        List<String> commands = client.getSupportedCommands();
        assertTrue("faulty respons", commands.contains("update_streams"));

        Mockito.when(transport.sendJson(ArgumentMatchers.anyString())).thenReturn(SUPPORTED_CMDS2);
        List<String> commands2 = client.getSupportedCommands();
        assertTrue("faulty respons", commands2.contains("update_streams"));
    }
}
