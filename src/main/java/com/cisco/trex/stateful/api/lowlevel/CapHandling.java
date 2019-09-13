package com.cisco.trex.stateful.api.lowlevel;

import com.cisco.trex.stateful.api.lowlevel.ASTFProgram.SideType;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

/** CapHandling to parse pcap file */
class CapHandling {
  /**
   * read pcap file
   *
   * @param fileName file name
   * @return CpcapReader
   */
  static CpcapReader cpcapReader(String fileName) {
    return new CpcapReader(fileName);
  }
}

/** a single pcap packet data mode class */
class CPacketData {
  private SideType direction;
  private byte[] payload;

  /**
   * construct
   *
   * @param direction
   * @param payload
   */
  CPacketData(SideType direction, byte[] payload) {
    this.direction = direction;
    this.payload = payload;
  }

  boolean isEmpty() {
    if (payload == null || payload.length == 0) {
      return true;
    }
    return false;
  }

  byte[] getPayload() {
    return payload;
  }

  SideType getDirection() {
    return direction;
  }

  CPacketData add(CPacketData cPacketData) {
    if (cPacketData.direction.equals(this.direction)) {
      return new CPacketData(this.direction, ArrayUtils.addAll(this.payload, cPacketData.payload));
    }
    throw new IllegalStateException(
        String.format(
            "input packet data direction: %s is different from current direction: %s",
            cPacketData.getDirection(), this.getDirection()));
  }
}

/** pcap reader class */
class CpcapReader {
  private CpcapReaderHelp obj;

  CpcapReader(String fileName) {
    this.obj = new CpcapReaderHelp(fileName);
  }

  void analyze() {
    obj.analyze();
  }

  void condensePktData() {
    obj.condensePktData();
  }

  boolean isTcp() {
    return obj.isTcp();
  }

  List<SideType> getPktDirs() {
    return obj.getDirs();
  }

  List<Double> getPktTimes() {
    return obj.getTimes();
  }

  List<CPacketData> getPkts() {
    return obj.getPkts();
  }

  InetAddress getClientIp() {
    return obj.getClientIp();
  }

  InetAddress getServerIp() {
    return obj.getServerIp();
  }

  int getSrcPort() {
    return obj.getSrcPort();
  }

  int getDstPort() {
    return obj.getDstPort();
  }

  int getCTcpWin() {
    return obj.getCTcpWin();
  }

  int getSTcpWin() {
    return obj.getSTcpWin();
  }

  int payloadLen() {
    return obj.getTotalPayloadLen();
  }
}

class CpcapReaderHelp {
  private static final HashMap<String, Integer> states = new HashMap<>();
  private static final String OTHER = "other";
  private static final String TCP = "tcp";
  private static final String UDP = "udp";
  private static final String INIT = "init";
  private static final String SYN = "syn";
  private static final String SYN_ACK = "syn+ack";

  static {
    states.put(INIT, 0);
    states.put(SYN, 1);
    states.put(SYN_ACK, 2);
  }

  private String fileName;
  private List<CPacketData> pkts;
  private List<Double> times;
  private List<SideType> dirs;
  private InetAddress clientIp;
  private InetAddress serverIp;
  private SideType direction;
  private String isTcp;
  private int sTcpWin;
  private int cTcpWin;
  private int srcPort;
  private int dstPort;
  private int totalPayloadLen;
  private boolean condensed;
  private int state;
  private boolean analyzed;

  CpcapReaderHelp(String fileName) {
    this.fileName = fileName;
    pkts = new ArrayList<>();
    times = new ArrayList<>();
    dirs = new ArrayList<>();
    clientIp = null;
    serverIp = null;
    isTcp = null;
    srcPort = -1;
    dstPort = -1;
    sTcpWin = -1;
    cTcpWin = -1;
    totalPayloadLen = 0;
    this.state = states.get("init");
    condensed = false;
    analyzed = false;
  }

  InetAddress getClientIp() {
    return clientIp;
  }

  InetAddress getServerIp() {
    return serverIp;
  }

  boolean isTcp() {
    return isTcp.equals(TCP);
  }

  List<CPacketData> getPkts() {
    return pkts;
  }

  List<SideType> getDirs() {
    return dirs;
  }

  List<Double> getTimes() {
    return times;
  }

  int getSrcPort() {
    return srcPort;
  }

  int getDstPort() {
    return dstPort;
  }

  int getCTcpWin() {
    return cTcpWin;
  }

  int getSTcpWin() {
    return sTcpWin;
  }

  int getTotalPayloadLen() {
    return totalPayloadLen;
  }

  /** condense data */
  void condensePktData() {
    if (condensed) {
      return;
    }
    CPacketData combinedData = null;

    List<CPacketData> newPkts = new ArrayList<>();
    List<SideType> newDirs = new ArrayList<>();

    for (CPacketData pkt : pkts) {
      if (pkt.getPayload() == null) {
        continue;
      }

      if (combinedData != null) {
        if (combinedData.getDirection().equals(pkt.getDirection())) {
          combinedData = combinedData.add(pkt);
        } else {
          newPkts.add(combinedData);
          newDirs.add(combinedData.getDirection());
          combinedData = pkt;
        }
      } else {
        combinedData = pkt;
      }
    }
    if (combinedData != null) {
      newPkts.add(combinedData);
      newDirs.add(combinedData.getDirection());
    }

    this.pkts = newPkts;
    this.dirs = newDirs;
    this.condensed = true;
  }

  /**
   * analyze pcap file.after analyzing,it will get all data from pacp file please use this method
   * before invoke get methods
   */
  void analyze() {
    if (analyzed) {
      return;
    }

    int index = 0; // index
    Packet nextEthPkt;

    String l4Type = null;
    double lastTime = 0;

    /** only for tcp usage */
    long expServerSeq = -1;
    long expClientSeq = -1;
    try (PcapHandle pcapHandle = Pcaps.openOffline(fileName)) {

      while (true) {
        double time;
        try {
          nextEthPkt = pcapHandle.getNextPacketEx(); // get next Ethernet packet
        } catch (Exception e) {
          break;
        }

        Timestamp timestamp = pcapHandle.getTimestamp();
        time = timestamp.getSeconds() + (double) timestamp.getNanos() / 1000000000; // time
        double dtime = time;
        double pktTime = 0;
        if (lastTime == 0) {
          pktTime = 0;
        } else {
          pktTime = dtime - lastTime;
        }
        lastTime = dtime;

        IpPacket l3;
        Packet next = nextEthPkt.getPayload(); // eth data, ip layer
        if (next instanceof IpV4Packet) {
          l3 = (IpV4Packet) next;
        } else if (next instanceof IpV6Packet) {
          l3 = (IpV6Packet) next;
        } else {
          throw new IllegalStateException(
              String.format(
                  "Error for file %s: Packet #%s in pcap is not IPv4 or IPv6!",
                  this.fileName, index));
        }

        // get ip packet header
        IpPacket.IpHeader ipHeader = l3.getHeader();

        if (clientIp == null) {
          clientIp = ipHeader.getSrcAddr();
          serverIp = ipHeader.getDstAddr();
          direction = SideType.Client;
        } else {
          if (clientIp.equals(ipHeader.getSrcAddr()) && serverIp.equals(ipHeader.getDstAddr())) {
            direction = SideType.Client;
          } else if (serverIp.equals(ipHeader.getSrcAddr())
              && clientIp.equals(ipHeader.getDstAddr())) {
            direction = SideType.Server;
          } else {
            this.fail(
                String.format(
                    "Only one session is allowed in a file. Packet %s is from different session",
                    index));
          }
        }

        Packet l4 = l3.getPayload(); // Transport Layer ,l4
        TcpPacket tcp = null;
        UdpPacket udp = null;

        if (l4 instanceof UdpPacket) {
          udp = (UdpPacket) l4;
        }
        if (l4 instanceof TcpPacket) {
          tcp = (TcpPacket) l4;
        }

        if (tcp == null && udp == null) {
          this.fail(String.format("Packet #%s in pcap has is not TCP or UDP", index));
        }

        if (tcp != null && udp != null) {
          this.fail(String.format("Packet #%s in pcap has both TCP and UDP", index));
        }

        String typel4 = getType(tcp, udp);

        if (isTcp == null) {
          isTcp = typel4;
        } else if (!isTcp.equals(typel4)) {
          this.fail(String.format("Packet #%s in pcap is %s and flow is %s", index, typel4, isTcp));
        }

        // TCP scenario
        if (tcp != null) {
          TcpPacket.TcpHeader l4Header = tcp.getHeader();

          // SYN , connection status
          if (l4Header.getSyn()) {
            // SYN is true & ACK is true
            if (l4Header.getAck()) {
              // s_tcp_opts =tcp.opts
              sTcpWin = l4Header.getWindowAsInt();
              if (state == states.get(INIT)) {
                this.fail(String.format("Packet #%s is SYN+ACK, but there was no SYN yet", index));
              } else if (state != states.get(SYN)) {
                this.fail(
                    String.format(
                        "Packet #%s is SYN+ACK, but there was already SYN+ACK in cap file", index));
              }
              state = states.get(SYN_ACK);
              expServerSeq = l4Header.getSequenceNumberAsLong() + 1;
            }
            // SYN - no ACK. Should be first packet client->server
            else {
              cTcpWin = l4Header.getWindowAsInt();
              expClientSeq = l4Header.getSequenceNumberAsLong() + 1;
              // allowing syn retransmission because cap2/https.pcap contains this
              if (state > states.get(SYN)) {
                this.fail(
                    String.format(
                        "Packet #%s is TCP SYN, but there was already TCP SYN in cap file", index));
              } else {
                state = states.get(SYN);
              }
            }
          } else if (state != states.get(SYN_ACK)) {
            this.fail("Cap file must start with syn, syn+ack sequence");
          }
          if (!(l4Type == null || l4Type.equals(TCP))) {
            this.fail(
                String.format(
                    "PCAP contains both TCP and %s. This is not supported currently.", l4Type));
          }
          l4Type = TCP;

          if (srcPort == -1) {
            srcPort = l4Header.getSrcPort().valueAsInt();
            dstPort = l4Header.getDstPort().valueAsInt();
          }
        }
        // UDP scenario
        else if (udp != null) {
          UdpPacket.UdpHeader l4Header = udp.getHeader();
          if (!(l4Type == null || l4Type.equals(UDP))) {
            this.fail(
                String.format(
                    "PCAP contains both UDP and %s. This is not supported currently.", l4Type));
          }
          l4Type = UDP;

          if (srcPort == -1) {
            srcPort = l4Header.getSrcPort().valueAsInt();
            dstPort = l4Header.getDstPort().valueAsInt();
          }
        } else {
          this.fail(String.format("Packet #%s in pcap is not TCP or UDP.", index));
        }

        int l4PayloadLen = 0;
        if (l4.getPayload() != null) {
          l4PayloadLen = l4.getPayload().length();
          totalPayloadLen += l4PayloadLen;
          pkts.add(new CPacketData(direction, l4.getPayload().getRawData()));
        } else {
          pkts.add(new CPacketData(direction, null));
        }
        times.add(pktTime);
        dirs.add(direction);

        // special handling for TCP FIN
        if (tcp != null && tcp.getHeader().getFin()) {
          l4PayloadLen = 1;
        }

        // verify there is no packet loss or retransmission in cap file
        // don't check for SYN
        if (tcp != null && !tcp.getHeader().getSyn()) {
          if (tcp.getHeader().getSrcPort().valueAsInt() == this.srcPort) {
            if (expClientSeq != tcp.getHeader().getSequenceNumberAsLong()) {
              this.fail(
                  String.format(
                      "TCP seq in packet %s is %s. We expected %s. Please check that there are no packet loss or retransmission in cap file",
                      index, tcp.getHeader().getSequenceNumberAsLong(), expClientSeq));
            }
            expClientSeq = tcp.getHeader().getSequenceNumberAsLong() + l4PayloadLen;
          } else {
            if (expServerSeq != tcp.getHeader().getSequenceNumberAsLong()) {
              this.fail(
                  String.format(
                      "TCP seq in packet %s is %s. We expected %s. Please check that there are no packet loss or retransmission in cap file",
                      index, tcp.getHeader().getSequenceNumberAsLong(), expServerSeq));
            }
            expServerSeq = tcp.getHeader().getSequenceNumberAsLong() + l4PayloadLen;
          }
        }
        index++;
      }
    } catch (PcapNativeException e) {
      throw new IllegalStateException("open pcap file failed", e);
    }
    this.analyzed = true;
  }

  private void fail(String msg) {
    throw new IllegalStateException(String.format("Error for file %s: %s", this.fileName, msg));
  }

  private static String getType(TcpPacket tcpPacket, UdpPacket udpPacket) {
    if (tcpPacket != null && udpPacket == null) {
      return TCP;
    }
    if (udpPacket != null && tcpPacket == null) {
      return UDP;
    }
    return OTHER;
  }
}
