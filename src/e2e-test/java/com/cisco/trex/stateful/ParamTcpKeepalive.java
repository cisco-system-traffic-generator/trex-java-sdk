package com.cisco.trex.stateful;

import com.cisco.trex.stateful.api.lowlevel.*;
import java.util.ArrayList;
import java.util.List;

public class ParamTcpKeepalive {

  public static ASTFProfile createProfile() {
    ASTFIpGenDist ipGenClient = new ASTFIpGenDist("16.0.0.0", "16.0.0.255");
    ASTFIpGenDist ipGenServer = new ASTFIpGenDist("48.0.0.0", "48.0.255.255");
    ASTFIpGen ipGen = new ASTFIpGen(ipGenClient, ipGenServer, new ASTFIpGenGlobal("1.0.0.0"));

    ASTFGlobalInfo globalInfoC = new ASTFGlobalInfo();
    globalInfoC.tcp(ASTFGlobalInfoBase.TcpParam.KEEP_INIT, 128);
    globalInfoC.tcp(ASTFGlobalInfoBase.TcpParam.KEEP_IDLE, 128);
    globalInfoC.tcp(ASTFGlobalInfoBase.TcpParam.KEEP_INTVL, 128);
    ASTFGlobalInfo globalInfoS = new ASTFGlobalInfo();
    globalInfoS.tcp(ASTFGlobalInfoBase.TcpParam.KEEP_INIT, 128);
    globalInfoS.tcp(ASTFGlobalInfoBase.TcpParam.KEEP_IDLE, 128);
    globalInfoS.tcp(ASTFGlobalInfoBase.TcpParam.KEEP_INTVL, 128);

    ASTFCapInfo capInfo =
        ASTFCapInfo.newBuilder()
            .filePath(AstfTestUtil.PCAP_DIR + "delay_10_http_browsing_0.pcap")
            .build();
    List<ASTFCapInfo> capList = new ArrayList();
    capList.add(capInfo);
    return new ASTFProfile(ipGen, globalInfoC, globalInfoS, new ArrayList<ASTFTemplate>(), capList);
  }
}
