package com.cisco.trex.stateful;

import com.cisco.trex.stateful.api.lowlevel.*;
import java.util.ArrayList;
import java.util.List;

public class FtpMultipleFlowsSim extends AstfTestUtil {

  static ASTFProfile createProfile(int fsize, int nflows, int tinc, String tgName) {
    tuneTcp(0);
    String ftpData = AstfTestUtil.getLoopStr("*", maxBufSize);
    int bSize = ftpData.length();
    int loop = fsize * 1024 * 1024 / maxBufSize;

    ASTFProgram programC = new ASTFProgram();
    if (tinc != 0) {
      programC.delayRand(0, tinc * 1000 * 1000);
    }
    programC.send(ftpGet);
    programC.setVar("var1", (long) loop);
    programC.setLabel("a:");
    programC.recv(maxBufSize, true);
    programC.jmpNz("var1", "a:");

    ASTFProgram programS = new ASTFProgram();
    programS.recv(ftpGet.length());
    programS.setVar("var2", (long) loop);
    programS.setLabel("a:");
    programS.send(ftpData);
    programS.jmpNz("var2", "a:");

    ASTFIpGenDist ipGenC =
        new ASTFIpGenDist("1.1.1.1", "1.1.1.100", ASTFIpGenDist.Distribution.SEQ, null);
    ASTFIpGenDist ipGenS =
        new ASTFIpGenDist("1.1.2.1", "1.1.2.100", ASTFIpGenDist.Distribution.SEQ, null);
    ASTFIpGen ipGen = new ASTFIpGen(ipGenC, ipGenS, new ASTFIpGenGlobal("1.0.0.0"));

    ASTFTCPClientTemplate templateC = new ASTFTCPClientTemplate(programC, ipGen, 20, nflows);
    ASTFTCPServerTemplate templateS =
        new ASTFTCPServerTemplate(programS, new ASTFAssociation(new ASTFAssociationRule(20)), null);
    ASTFTemplate template = new ASTFTemplate(templateC, templateS);
    List<ASTFTemplate> templateList = new ArrayList();
    templateList.add(template);

    ASTFProfile profile = new ASTFProfile(ipGen, gInfo, gInfo, templateList, null);
    return profile;
  }
}
