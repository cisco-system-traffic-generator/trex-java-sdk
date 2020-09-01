package com.cisco.trex.stateful;

import com.cisco.trex.stateful.api.lowlevel.*;
import java.util.ArrayList;
import java.util.List;

public class HttpSimple {
  private static String cIpStart = "16.0.0.0";
  private static String cIpEnd = "16.0.0.255";
  private static String sIpStart = "48.0.0.0";
  private static String sIpEnd = "48.0.255.255";
  private static String ipOffset = "1.0.0.0";
  private static float cps = 2.776f;
  private static ASTFIpGenDist.Distribution distribution = ASTFIpGenDist.Distribution.SEQ;

  public static ASTFProfile getProfile() {
    ASTFIpGenDist ipGenClient = new ASTFIpGenDist(cIpStart, cIpEnd, distribution, null);
    ASTFIpGenDist ipGenServer = new ASTFIpGenDist(sIpStart, sIpEnd, distribution, null);
    ASTFIpGen ipGen = new ASTFIpGen(ipGenClient, ipGenServer, new ASTFIpGenGlobal(ipOffset));
    ASTFCapInfo capInfo =
        ASTFCapInfo.newBuilder()
            .filePath(AstfTestUtil.PCAP_DIR + "delay_10_http_browsing_0.pcap")
            .cps(cps)
            .build();
    List<ASTFCapInfo> capList = new ArrayList();
    capList.add(capInfo);
    return new ASTFProfile(ipGen, null, null, new ArrayList<ASTFTemplate>(), capList);
  }
}
