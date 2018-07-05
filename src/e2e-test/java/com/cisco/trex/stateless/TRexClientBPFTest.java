package com.cisco.trex.stateless;


import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.exception.TRexTimeoutException;
import com.cisco.trex.stateless.model.*;
import com.cisco.trex.stateless.model.capture.*;
import org.junit.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class TRexClientBPFTest {
    public static final String CLIENT_USER = "unit-tests-user";
    private static final Integer STREAM_ID = 100500;
    public static TRexClient client;

    @BeforeClass
    public static void setUp() throws TRexConnectionException, TRexTimeoutException {
        client = new TRexClient("trex-host", "4501", CLIENT_USER);
        client.connect();
    }

    @Test
    public void captureMonitorFilterTest() throws InterruptedException, IllegalRawDataException {
        filterTestBase((f) -> {
            List<Integer> rxPorts = client.getPorts().stream().map(Port::getIndex).collect(Collectors.toList());
            List<Integer> txPorts = client.getPorts().stream().map(Port::getIndex).collect(Collectors.toList());
            return client.captureMonitorStart(rxPorts, txPorts, f);
        });
    }

    @Test
    public void captureRecorderFilterTest() throws InterruptedException, IllegalRawDataException {
        filterTestBase((f) -> {
            List<Integer> rxPorts = client.getPorts().stream().map(Port::getIndex).collect(Collectors.toList());
            List<Integer> txPorts = client.getPorts().stream().map(Port::getIndex).collect(Collectors.toList());
            return client.captureRecorderStart(rxPorts, txPorts, f, 1000);
        });
    }

    private void filterTestBase(CaptureMonitorStarter monitorStarter) throws InterruptedException, IllegalRawDataException {
        List<Port> ports = client.getPorts();
        Port port = ports.get(0);
        client.acquirePort(port.getIndex(), true);
        client.serviceMode(port.getIndex(), true);
        client.removeAllCaptures();
        client.removeAllStreams(port.getIndex());

        final String srcMacA = "11:00:00:00:00:11";
        final String srcMacB = "33:00:00:00:00:33";

        client.addStream(port.getIndex(), TRexClientTest.buildStream(TRexClientTest.buildArpPkt(srcMacA)));
        client.addStream(port.getIndex(), TRexClientTest.buildStream(TRexClientTest.buildArpPkt(srcMacB)));

        Map<String, Object> mul = new HashMap<>();
        mul.put("op", "abs");
        mul.put("type", "pps");
        mul.put("value", 1.0);

        CaptureTester unfiltered = new CaptureTester(monitorStarter, "");
        CaptureTester filteredA = new CaptureTester(monitorStarter, "ether src " + srcMacA);
        CaptureTester filteredB = new CaptureTester(monitorStarter, "ether src " + srcMacB);
        Assert.assertTrue(client.getActiveCaptures().get().length == 3);

        client.startTraffic(port.getIndex(), -1.0, true, mul, 1);

        sleep(3000);

        client.stopTraffic(port.getIndex());
        unfiltered.StopAndFetch();
        filteredA.StopAndFetch();
        filteredB.StopAndFetch();

        Assert.assertTrue(unfiltered.PacketsCount() != 0);
        Assert.assertTrue(filteredA.PacketsCount() != 0);
        Assert.assertTrue(filteredB.PacketsCount() != 0);

        Assert.assertTrue(filteredA.PacketsWithSrcCount(srcMacA) == filteredA.PacketsCount());
        Assert.assertTrue(filteredB.PacketsWithSrcCount(srcMacB) == filteredB.PacketsCount());
        Assert.assertTrue(filteredA.PacketsWithSrcCount(srcMacB) == 0);
        Assert.assertTrue(filteredB.PacketsWithSrcCount(srcMacA) == 0);

        int ABInUnfiltered = unfiltered.PacketsWithSrcCount(srcMacA) + unfiltered.PacketsWithSrcCount(srcMacB);
        Assert.assertTrue(ABInUnfiltered == filteredA.PacketsCount() + filteredB.PacketsCount());
    }

    @FunctionalInterface
    private interface CaptureMonitorStarter {
        TRexClientResult<CaptureMonitor> start(String filter);
    }

    private class CaptureTester {
        CaptureMonitor monitor;
        CaptureInfo info;
        CapturedPackets packets;

        public CaptureTester(CaptureMonitorStarter s, String filter) {
            TRexClientResult<CaptureMonitor> startResult = s.start(filter);
            Assert.assertFalse(startResult.isFailed());
            monitor = startResult.get();
        }

        public void StopAndFetch() {
            client.captureMonitorStop(monitor.getCaptureId());
            info = Arrays.stream(client.getActiveCaptures().get())
                    .filter(cap -> cap.getId() == monitor.getCaptureId()).findFirst().get();
            TRexClientResult<CapturedPackets> result = client.captureFetchPkts(info.getId(), info.getCount());
            Assert.assertFalse(result.isFailed());
            packets = result.get();
        }

        public int PacketsWithSrcCount(String srcAddr) throws IllegalRawDataException {
            int total = 0;
            for(CapturedPkt p : packets.getPkts()) {
                byte[] pktBin = Base64.getDecoder().decode(p.getBinary());
                EthernetPacket etherPkt = EthernetPacket.newPacket(pktBin, 0, pktBin.length);

                if(etherPkt.getHeader().getSrcAddr().toString().equalsIgnoreCase(srcAddr))
                    total++;
            }
            return total;
        }

        public int PacketsCount() {
            return info.getCount();
        }
    }
}
