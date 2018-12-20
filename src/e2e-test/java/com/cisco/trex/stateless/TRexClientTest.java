package com.cisco.trex.stateless;


import com.cisco.trex.stateless.exception.ServiceModeRequiredException;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.exception.TRexTimeoutException;
import com.cisco.trex.stateless.model.*;
import com.cisco.trex.stateless.model.capture.CaptureInfo;
import com.cisco.trex.stateless.model.capture.CaptureMonitor;
import com.cisco.trex.stateless.model.capture.CaptureMonitorStop;
import com.cisco.trex.stateless.model.capture.CapturedPackets;
import com.cisco.trex.stateless.model.port.PortVlan;
import com.cisco.trex.stateless.model.vm.VMInstruction;

import org.junit.*;
import org.pcap4j.packet.ArpPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class TRexClientTest {
    public static final String CLIENT_USER = "unit-tests-user";
    private static final Packet SIMPLE_PACKET = buildArpPkt("00:50:56:94:21:df");
    private static final Integer STREAM_ID = 100500;
    public static TRexClient client;

    @BeforeClass
    public static void setUp() throws TRexConnectionException, TRexTimeoutException {
        client = new TRexClient("trex-host", "4501", CLIENT_USER);
        client.connect();
    }

    @Test
    public void setVlanTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        client.acquirePort(port.index, true);
        TRexClientResult<StubResult> result = client.setVlan(port.index, Arrays.asList(23, 34));
        Assert.assertTrue(!result.isFailed());

        PortStatus portStatus = getPortStatus(port.index);
        PortVlan vlan = portStatus.getAttr().getVlan();
        Assert.assertTrue(vlan.tags.get(0).equals(23));
        Assert.assertTrue(vlan.tags.get(1).equals(34));
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
        PortStatus portStatus = getPortStatus(0);
        Assert.assertTrue(portStatus.getAttr().getLink().getUp());
    }

    @Test
    public void getPortStatusThreadSafeTest() {
        new Thread(() -> getPortStatus(0)).start();
        new Thread(() -> getPortStatus(1)).start();
        new Thread(() -> getPortStatus(0)).start();
        new Thread(() -> getPortStatus(1)).start();
    }

    private PortStatus getPortStatus(int portIdx) {
        try{
            TRexClientResult<PortStatus> result = client.getPortStatus(portIdx);
            if (result.isFailed()) {
                Assert.fail(result.getError());
            }
            return result.get();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            return null;
        }
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

    /**
     * This is a specific test which is not related to standalone Java SDK
     */
    @Test
    @Ignore
    public void pingTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        try {
            client.acquirePort(port.getIndex(), true);
            client.serviceMode(port.getIndex(), true);
            String host = "192.168.9.1";
            EthernetPacket icmpReply = client.sendIcmpEcho(port.getIndex(), host, 10, 10, 1000);
            Assert.assertNotNull(icmpReply);
        } catch (UnknownHostException ignored) {
        } finally {
            client.serviceMode(port.getIndex(), false);
            client.releasePort(port.getIndex());
        }
    }

    /**
     * This is a specific test which is not related to standalone Java SDK
     */
    @Test
    @Ignore
    public void setL3Test() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        try {
            client.acquirePort(port.getIndex(), true);
            client.serviceMode(port.getIndex(), true);
            client.setL3Mode(port.getIndex(), null, "192.168.9.27", "192.168.9.28");
            String nextHopMac = client.resolveArp(port.getIndex(), "192.168.9.27", "192.168.9.28");
            boolean result = client.setL3Mode(port.getIndex(), nextHopMac, "192.168.9.27", "192.168.9.28");
            Assert.assertTrue(result);
        } finally {
            client.serviceMode(port.getIndex(), false);
            client.releasePort(port.getIndex());
        }
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
        
        PortStatus portStatusTX = client.getPortStatus(port.getIndex()).get();
        Assert.assertTrue(portStatusTX.state.equals("TX"));
        client.stopTraffic(port.getIndex());

        PortStatus portStatusStream = client.getPortStatus(port.getIndex()).get();
        Assert.assertTrue(portStatusStream.state.equals("STREAMS"));
        
        client.releasePort(port.getIndex());
    }

    /**
     * This is a specific test which is not related to standalone Java SDK
     */
    @Test
    @Ignore
    public void arpTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        Port port1 = ports.get(1);
        client.acquirePort(port.getIndex(), true);
        client.acquirePort(port1.getIndex(), true);
        
        client.serviceMode(port.getIndex(), true);
        client.serviceMode(port1.getIndex(), true);

        client.removeRxQueue(port.getIndex());
        client.setRxQueue(port.getIndex(), 5);
            
        Packet pkt = SIMPLE_PACKET;
        client.sendPacket(port.getIndex(), pkt);

        Predicate<EthernetPacket> arpReplyFilter = etherPkt -> {
            if(etherPkt.contains(ArpPacket.class)) {
                ArpPacket arp = (ArpPacket) etherPkt.getPayload();
                if (ArpOperation.REPLY.equals(arp.getHeader().getOperation())) {
                    return true;
                }
            }
            return false;
        };
        List<EthernetPacket> pkts = client.getRxQueue(port.getIndex(), arpReplyFilter);
        client.removeRxQueue(port.getIndex());
        client.serviceMode(port.getIndex(), false);
        client.serviceMode(port1.getIndex(), false);
        Assert.assertTrue(pkts.size() > 0);
    }

    @Test
    public void getCapturesTest() {
        List<Port> ports = client.getPorts();

        for(Port port : ports) {
            client.acquirePort(port.getIndex(), true);
            client.serviceMode(port.getIndex(), true);
        }

        client.removeAllCaptures();
        
        TRexClientResult<CaptureMonitor> result = startMonitor(ports);
        Assert.assertFalse(result.isFailed());

        CaptureMonitor monitor = result.get();
        Assert.assertTrue(monitor.getCaptureId() > 0);

        TRexClientResult<CaptureInfo[]> activeCaptures = client.getActiveCaptures();
        Optional<CaptureInfo> monitorInfoResult = Arrays.stream(activeCaptures.get())
                .filter(info -> info.getId() == monitor.getCaptureId())
                .findFirst();

        Assert.assertTrue(monitorInfoResult.isPresent());
    }
    
    private TRexClientResult<CaptureMonitor> startMonitor(List<Port> portList) {
        List<Integer> rxPorts = new LinkedList<Integer>();
        if (portList.size()>0) {
            rxPorts.add(portList.get(0).getIndex());
        }
        List<Integer> txPorts = portList.stream().map(Port::getIndex).collect(Collectors.toList());
        return client.captureMonitorStart(rxPorts, txPorts, "");
    }

    @Test
    public void startRecorderTest() {
        List<Port> ports = client.getPorts();

        for(Port port : ports) {
            client.acquirePort(port.getIndex(), true);
            client.serviceMode(port.getIndex(), true);
        }

        TRexClientResult<CaptureMonitor> result = startMonitor(ports);
        
        Assert.assertFalse(result.isFailed());

        CaptureMonitor capture = result.get();

        TRexClientResult<CaptureInfo[]> activeCaptures = client.getActiveCaptures();
        Optional<CaptureInfo> recordMonitor = Arrays.stream(activeCaptures.get())
                .filter(info -> info.getId() == capture.getCaptureId())
                .findFirst();
        
        Assert.assertTrue(recordMonitor.isPresent());
        Assert.assertEquals("ACTIVE", recordMonitor.get().getState());
    }
    
    @Test
    public void removeAllRecorderTest() {
        TRexClientResult<List<RPCResponse>> result = client.removeAllCaptures();
        Assert.assertFalse(result.isFailed());
    }
    
    @Test
    public void sendMultipleCmdsTest() {
        List<TRexCommand> commands = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            commands.add(client.buildCommand("sendIcmpV6Request", new HashMap<>()));
        }

        TRexClientResult<List<RPCResponse>> result = client.callMethods(commands);
        
        Assert.assertFalse(result.isFailed());
        
        Assert.assertEquals(commands.size(), result.get().size());
    }
    
    @Test
    public void captureRecordStopTest() {
        List<Port> ports = client.getPorts();

        for(Port port : ports) {
            client.acquirePort(port.getIndex(), true);
            client.serviceMode(port.getIndex(), true);
        }

        TRexClientResult<CaptureMonitor> result = startMonitor(client.getPorts());
        
        CaptureMonitor recordMonitor = result.get();

        TRexClientResult<CaptureMonitorStop> stopResult = client.captureMonitorStop(result.get().getCaptureId());
        Assert.assertFalse(stopResult.isFailed());

        TRexClientResult<CaptureInfo[]> activeCapturesResult = client.getActiveCaptures();
        Assert.assertFalse(activeCapturesResult.isFailed());

        Optional<CaptureInfo> stoppedMonitor = Arrays.stream(activeCapturesResult.get())
                .filter(captureInfo -> captureInfo.getId() == recordMonitor.getCaptureId())
                .findFirst();
        
        Assert.assertTrue(stoppedMonitor.isPresent());
        
        Assert.assertEquals("STOPPED", stoppedMonitor.get().getState());
    }
    
    @Test
    public void fetchCapturedPKtsTest() throws InterruptedException {
        List<Port> ports = client.getPorts();

        for (Port port: ports) {
            client.acquirePort(port.getIndex(), true);
            client.serviceMode(port.getIndex(), true);
        }

        Stream s = buildStream(SIMPLE_PACKET);
        client.addStream(ports.get(0).getIndex(), s);


        TRexClientResult<CaptureMonitor> result = startMonitor(ports);

        Map<String, Object> mul = new HashMap<>();
        mul.put("op", "abs");
        mul.put("type", "pps");
        mul.put("value", 1.0);
        client.startTraffic(ports.get(0).getIndex(), -1.0, true, mul, 1);
        sleep(3000);
        client.stopTraffic(ports.get(0).getIndex());

        TRexClientResult<CapturedPackets> capturedPktsResult = client.captureFetchPkts(result.get().getCaptureId(), 10);
        
        Assert.assertFalse(capturedPktsResult.isFailed());
        CapturedPackets capturedPkts = capturedPktsResult.get();
        
        Assert.assertTrue(capturedPkts.getPkts().size() > 0);
        
    }

    @Test
    public void getAllStreamsTest() {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        Stream stream1 = buildStream(SIMPLE_PACKET);
        Stream stream2 = buildStream(SIMPLE_PACKET);
        client.acquirePort(port.getIndex(), true);
        client.addStream(port.getIndex(), stream1);
        client.addStream(port.getIndex(), stream2);

        List<Stream> allStreams = client.getAllStreams(port.getIndex());


        for (Stream str :
                allStreams) {
            Optional<Stream> found = allStreams.stream().filter(stream -> stream.getId().equals(str.getId())).findFirst();
            if (!found.isPresent()) {
                Assert.fail(String.format("Stream with id %s was not found", str.getId()));
            }

            Stream foundStream = found.get();
            Assert.assertEquals(foundStream, str);
        }

        client.releasePort(port.getIndex());
    }

    @Test
    public void iPV6ScanTest() {
        List<Port> ports = client.getPorts();

        client.acquirePort(ports.get(0).getIndex(), true);
        client.serviceMode(ports.get(0).getIndex(), true);

        try {
            Map<String, Ipv6Node> ipv6Nodes = client.scanIPv6(ports.get(0).getIndex());
            Assert.assertTrue(ipv6Nodes.size() > 0);
        } catch (ServiceModeRequiredException e) {
            Assert.fail("Port 0 is not in service mode");
        }
    }
    
    @Test
    public void sendIcmpV6EchoTest() {
        List<Port> ports = client.getPorts();

        client.acquirePort(ports.get(0).getIndex(), true);
        client.serviceMode(ports.get(0).getIndex(), true);

        try {
            // fe80:0:0:0:822a:a8ff:fe56:1517
            // fe80:0:0:0:bc:18ff:fe6a:c570
            // fe80:0:0:0:250:56ff:fe9b:2ef7
            Assert.assertTrue(client.sendIcmpV6Echo(ports.get(0).getIndex(), "fe80::822a:a8ff:fec0:5495", 0, 0, 2) != null);
        } catch (ServiceModeRequiredException e) {
            Assert.fail("Port 0 is not in service mode");
        }
    }
    
    @Test
    @Ignore
    public void sendPacketTest() throws TRexTimeoutException{
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        
        client.acquirePort(port.getIndex(), true);
        client.serviceMode(port.getIndex(), true);
        client.setRxQueue(port.getIndex(), 10);
        List<EthernetPacket> pkts = client.getRxQueue(port.getIndex(), p -> true);
    }
    
    private static EthernetPacket buildIdealPkt(String pkt) {
        byte[] pktBin = Base64.getDecoder().decode(pkt);
        try {
            return EthernetPacket.newPacket(pktBin, 0, pktBin.length);
        } catch (IllegalRawDataException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @AfterClass
    public static void tierDown() {
        client.getPorts().stream().forEach(port -> client.stopTraffic(port.getIndex()));
        client.disconnect();
    }

    public static Stream buildStream(Packet pkt) {
        return new Stream(
                (int) (Math.random() * 1000),
                true,
                3,
                0.0,
                createStreamMode(),
                -1,
                pkt,
                new StreamRxStats(true, true, true, STREAM_ID),
                new StreamVM("", Collections.<VMInstruction>emptyList()),
                true,
                false,
                null
        );
    }

    private static StreamMode createStreamMode() {
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

    public static EthernetPacket buildArpPkt(String srcMacStr) {
        ArpPacket.Builder arpBuilder = new ArpPacket.Builder();
        MacAddress srcMac = MacAddress.getByName(srcMacStr);
        try {
            String strSrcIpAddress = "192.168.9.27";
            String strDstIpAddress = "192.168.9.28";
            arpBuilder
                    .hardwareType(ArpHardwareType.ETHERNET)
                    .protocolType(EtherType.IPV4)
                    .hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
                    .protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES)
                    .operation(ArpOperation.REQUEST)
                    .srcHardwareAddr(srcMac)
                    .srcProtocolAddr(InetAddress.getByName(strSrcIpAddress))
                    .dstHardwareAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
                    .dstProtocolAddr(InetAddress.getByName(strDstIpAddress));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }

        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder.dstAddr(MacAddress.ETHER_BROADCAST_ADDRESS)
                .srcAddr(srcMac)
                .type(EtherType.ARP)
                .payloadBuilder(arpBuilder)
                .paddingAtBuild(true);

        return etherBuilder.build();
    }
}
