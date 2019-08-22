package com.cisco.trex.stateful.api.lowlevel;

import java.util.Base64;

/** Java implementation for TRex python sdk ASTFCmdTxPkt class */
public class ASTFCmdTxPkt extends ASTFCmd {
  private static final String NAME = "tx_msg";

  private String base64Buf;
  private int bufLen;

  /**
   * construct
   *
   * @param asciiBuf
   */
  public ASTFCmdTxPkt(byte[] asciiBuf) {
    super();
    this.base64Buf = encodeBase64(asciiBuf);
    fields.addProperty("name", NAME);
    fields.addProperty("buf_index", -1);
    this.bufLen = asciiBuf.length;
    stream = false;
    buffer = true;
  }

  /**
   * get buf length
   *
   * @return buf length
   */
  public int getBufLen() {
    return bufLen;
  }

  /**
   * get buf
   *
   * @return encoded base64 buf
   */
  public String buf() {
    return base64Buf;
  }

  /**
   * set buf index
   *
   * @param index
   */
  public void setbufIndex(int index) {
    fields.addProperty("buf_index", index);
  }

  @Override
  public String getName() {
    return NAME;
  }

  /**
   * get buf length
   *
   * @return bufLen
   */
  public int bufLen() {
    return bufLen;
  }

  private String encodeBase64(byte[] bytes) {
    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(bytes);
  }
}
