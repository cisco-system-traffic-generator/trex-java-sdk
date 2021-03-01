package com.cisco.trex.stateful;

import com.cisco.trex.stateful.api.lowlevel.*;
import java.util.ArrayList;
import java.util.List;

public class MixPcapFileAstfTraffic {

  public static ASTFProfile createProfile() {
    ASTFCapInfo capInfo1 =
        ASTFCapInfo.newBuilder().filePath(AstfTestUtil.PCAP_DIR + "http_browsing.pcap").build();
    ASTFCapInfo capInfo2 =
        ASTFCapInfo.newBuilder()
            .filePath(AstfTestUtil.PCAP_DIR + "delay_10_mail_pop_0.pcap")
            .build();
    List<ASTFCapInfo> capList = new ArrayList();
    capList.add(capInfo1);
    capList.add(capInfo2);

    ASTFIpGenDist ipGenClient = new ASTFIpGenDist("1.0.0.0", "1.0.0.1");
    ASTFIpGenDist ipGenServer = new ASTFIpGenDist("2.0.0.0", "2.0.0.1");
    ASTFIpGen ipGen = new ASTFIpGen(ipGenClient, ipGenServer, new ASTFIpGenGlobal("1.0.0.0"));

    return new ASTFProfile(ipGen, null, null, new ArrayList<ASTFTemplate>(), capList);
  }
}
