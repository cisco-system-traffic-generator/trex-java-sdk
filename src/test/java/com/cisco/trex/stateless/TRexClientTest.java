package com.cisco.trex.stateless;


import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.exception.TRexTimeoutException;
import com.cisco.trex.stateless.model.*;
import com.google.gson.Gson;
import org.junit.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TRexClientTest {
    public static final String CLIENT_USER = "unit-tests-user";
    private static final Packet SIMPLE_PACKET = new Packet("AAAAAQAAAAAAAgAACABFAAAoAAEAAEAGOs4QAAABMAAAAQQBBAEAAff6AAAAAFAAIACz4wAA");
    private static final Packet PING_PACKET = new Packet("AFBWiv6jpF5g0BfXCABFAAAcAAEAAEAB8kfAqANswKgD3AgA9+MAHAAA");
    private static final Integer STREAM_ID = 100500;
    public static Gson gson = new Gson();
    public static TRexClient client = null;

    @BeforeClass
    public static void setUp() throws TRexConnectionException, TRexTimeoutException {
        client = new TRexClient("tcp", "trex-xored", "4501", CLIENT_USER);
        client.connect();
    }

    @Test
    public void getPortsTest() {
        List<Port> ports = client.getPorts();
        Assert.assertTrue(ports.size() > 0);
    }

    @Test
    public void getSupportedCommandsTest() {
        List<String> cmds = client.getSupportedCommands();
        Assert.assertTrue(cmds.size() > 0);
    }

    @Test
    public void getPortStatusTest() {
        PortStatus portStatus = client.getPortStatus(0);
        Assert.assertTrue(portStatus.linkUp);
    }

    @Test
    public void acquirePortTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        PortStatus portStatus = client.acquirePort(port.getIndex(), true);
        Assert.assertEquals(CLIENT_USER, portStatus.owner);
    }

    @Test
    public void releasePortTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        client.acquirePort(port.getIndex(), true);
        PortStatus releasedPortStatus = client.releasePort(port.getIndex());
        Assert.assertNotEquals(CLIENT_USER, releasedPortStatus.owner);
    }

    @Test
    public void serviceModeOnOffTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        
        client.acquirePort(port.getIndex(), true);
        PortStatus serviceModePortStatus = client.serviceMode(port.getIndex(), true);
        Assert.assertTrue(serviceModePortStatus.service);
        
        client.serviceMode(port.getIndex(), false);
        PortStatus normalModePortStatus = client.serviceMode(port.getIndex(), false);
        Assert.assertFalse(normalModePortStatus.service);
    }

    @Test
    public void addStreamTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        Stream stream = buildStream(SIMPLE_PACKET);
        client.acquirePort(port.getIndex(), true);
        client.addStream(port.getIndex(), stream);
        
        List<Integer> streamIds = client.getStreamIds(port.getIndex());
        Assert.assertTrue(streamIds.contains(stream.getId()));
        client.releasePort(port.getIndex());
    }
    

    @Test
    public void removeStreamTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        Stream stream = buildStream(SIMPLE_PACKET);
        
        client.acquirePort(port.getIndex(), true);
        client.addStream(port.getIndex(), stream);
        client.removeStream(port.getIndex(), stream.getId());
        
        List<Integer> streamIds = client.getStreamIds(port.getIndex());
        Assert.assertTrue(!streamIds.contains(stream.getId()));
        
        client.releasePort(port.getIndex());
    }

    @Test
    public void removeAllStreamsTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        
        Stream s = buildStream(SIMPLE_PACKET);
        
        client.acquirePort(port.getIndex(), true);
        client.addStream(port.getIndex(), s);
        client.removeAllStreams(port.getIndex());
        
        List<Integer> streamIds = client.getStreamIds(port.getIndex());
        Assert.assertTrue(streamIds.isEmpty());
        
        client.releasePort(port.getIndex());
    }

    @Test
    public void getStreamTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        
        Stream s = buildStream(SIMPLE_PACKET);
        client.acquirePort(port.getIndex(), true);
        client.addStream(port.getIndex(), s);
        
        Stream streamFromTrex = client.getStream(port.getIndex(), s.getId());
        Assert.assertTrue(s.equals(streamFromTrex));
        
        client.releasePort(port.getIndex());
    }

    @Test
    public void startStopTrafficTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        client.acquirePort(port.getIndex(), true);

        Stream s = buildStream(SIMPLE_PACKET);
        client.addStream(port.getIndex(), s);

        Map<String, Object> mul = new HashMap<>();
        mul.put("op", "abs");
        mul.put("type", "pps");
        mul.put("value", 1.0);
        client.startTraffic(port.getIndex(), -1.0, true, mul, 1);
        
        PortStatus portStatusTX = client.getPortStatus(port.getIndex());
        Assert.assertTrue(portStatusTX.state.equals("TX"));
        client.stopTraffic(port.getIndex());

        PortStatus portStatusStream = client.getPortStatus(port.getIndex());
        Assert.assertTrue(portStatusStream.state.equals("STREAMS"));
        
        client.releasePort(port.getIndex());
    }
    
    @Test
    @Ignore
    public void sendPacketTest() throws TRexTimeoutException{
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        
        client.acquirePort(port.getIndex(), true);
        client.serviceMode(port.getIndex(), true);
        client.setRxQueue(port.getIndex(), 10);
        
        List<org.pcap4j.packet.Packet> pkts = client.getRxQueue(port.getIndex(), p -> true);
    }
    
    @AfterClass
    public static void tierDown() {
        client.getPorts().stream().forEach(port -> client.stopTraffic(port.getIndex()));
        client.disconnect();
    }

    private Stream buildStream(Packet pkt) {
        return new Stream(
                (int) (Math.random() * 1000),
                true,
                0.0,
                createStreamMode(),
                -1,
                pkt,
                new StreamRxStats(true, true, true, STREAM_ID),
                new StreamVM("", Collections.<VMInstruction>emptyList()),
                true
        );
    }

    private StreamMode createStreamMode() {
        return new StreamMode(
                10000,
                200,
                0,
                1000.0,
                new StreamModeRate(
                        StreamModeRate.Type.pps,
                        1000.0
                ),
                StreamMode.Type.continuous
        );
    }
}
