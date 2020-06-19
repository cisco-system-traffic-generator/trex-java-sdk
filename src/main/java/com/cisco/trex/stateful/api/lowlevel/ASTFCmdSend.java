package com.cisco.trex.stateful.api.lowlevel;

import java.util.Base64;

/** Java implementation for TRex python sdk ASTFCmdSend class */
public class ASTFCmdSend extends ASTFCmd {
  private static final String NAME = "tx";

  private String base64Buf;
  private int bufLen;

  /**
   * construct
   *
   * @param asciiBuf
   */
  public ASTFCmdSend(byte[] asciiBuf) {
      this(asciiBuf, 0, null);
  }

  /**
   * construct
   *
   * @param asciiBuf
   * @param size
   * @param fill
   */
  public ASTFCmdSend(byte[] asciiBuf, int size, byte[] fill) {
    super();
    String bufStr = encodeBase64(asciiBuf);
    this.base64Buf = bufStr;
    fields.addProperty("name", NAME);
    fields.addProperty("buf_index", -1);
    this.bufLen = asciiBuf.length;
    if (size > asciiBuf.length) {
        this.base64Buf = "{ \"base\": \"" + bufStr + "\", \"size\": " + size + " }";
        if (fill != null) {
            this.base64Buf = "{ \"base\": \"" + bufStr + "\", \"fill\": \"" + encodeBase64(fill) + "\", \"size\": " + size + " }";
        }
        this.bufLen = size;
    }
    stream = true;
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
   * get buf index
   *
   * @return buf index
   */
  public int getBufIndex() {
    return fields.get("buf_index").getAsInt();
  }

  /**
   * set buf index
   *
   * @param index
   */
  public void setbufIndex(int index) {
    fields.addProperty("buf_index", index);
  }

  /**
   * get buf
   *
   * @return encoded base64 buf
   */
  public String buf() {
    return base64Buf;
  }

  private static String encodeBase64(byte[] bytes) {
    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(bytes);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
