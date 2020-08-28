package com.cisco.trex.stateful;

import com.cisco.trex.stateful.api.lowlevel.*;
import java.util.ArrayList;
import java.util.List;

public class ParamTcpDelayAck {
  public static ASTFProfile createProfile() {
    ASTFIpGenDist ipGenClient = new ASTFIpGenDist("16.0.0.0", "16.0.0.255");
    ASTFIpGenDist ipGenServer = new ASTFIpGenDist("48.0.0.0", "48.0.255.255");
    ASTFIpGen ipGen = new ASTFIpGen(ipGenClient, ipGenServer, new ASTFIpGenGlobal("1.0.0.0"));

    ASTFGlobalInfo globalInfoC = new ASTFGlobalInfo();
    globalInfoC.tcp(ASTFGlobalInfoBase.TcpParam.DELAY_ACK_MSEC, 50);
    ASTFGlobalInfo globalInfoS = new ASTFGlobalInfo();
    globalInfoS.tcp(ASTFGlobalInfoBase.TcpParam.DELAY_ACK_MSEC, 50);

    ASTFCapInfo capInfo =
        ASTFCapInfo.newBuilder()
            .filePath(AstfTestUtil.PCAP_DIR + "delay_10_http_browsing_0.pcap")
            .build();
    List<ASTFCapInfo> capList = new ArrayList();
    capList.add(capInfo);
    return new ASTFProfile(ipGen, globalInfoC, globalInfoS, new ArrayList<ASTFTemplate>(), capList);
  }
}
