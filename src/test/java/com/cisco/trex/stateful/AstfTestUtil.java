package com.cisco.trex.stateful;

import com.cisco.trex.stateful.api.lowlevel.*;
import com.cisco.trex.stateful.api.lowlevel.ASTFGlobalInfoBase.TcpParam;

public class AstfTestUtil {

  public static String PCAP_DIR = "/local/users/ecennng/pcap/";
  public static int maxBufSize = 0;
  public static String ftpGet = "RETR file";

  public static final String DEFAULT_IP_OFFSET = "1.0.0.0";
  public static ASTFGlobalInfo gInfo = new ASTFGlobalInfo();

  public static String getLoopStr(String str, int loop) {
    if (loop <= 0) {
      return "";
    } else if (loop == 1) {
      return str;
    } else {
      StringBuilder sb = (new StringBuilder(str.length() * loop)).append(str);

      int i;
      for (i = 1; i * 2 <= loop; i *= 2) {
        sb.append(sb);
      }

      return sb.toString() + getLoopStr(str, loop - i);
    }
  }

  public static void tuneTcp(int mass) {
    maxBufSize = 512 * 1024;
    if (mass != 0) {
      gInfo.tcp(ASTFGlobalInfoBase.TcpParam.MSS, mass);
    }
    gInfo.tcp(TcpParam.NO_DELAY, 1);
    gInfo.tcp(TcpParam.RX_BUF_SIZE, 512 * 1024);
    gInfo.tcp(TcpParam.TX_BUF_SIZE, 512 * 1024);
    gInfo.tcp(TcpParam.DO_RFC1323, 0);
  }

  public static int[] calcLoops(int buffer, int loops) {
    int maxMul = (int) (Math.round((double) Long.parseLong("ffffffff", 16) / buffer) / 4);
    double div = loops / maxMul;
    if (div < 1.0d) {
      return new int[] {loops * buffer, 0, 0};
    }
    return null;
  }
}
