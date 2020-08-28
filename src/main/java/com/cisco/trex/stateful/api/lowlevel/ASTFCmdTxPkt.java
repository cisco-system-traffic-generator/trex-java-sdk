package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdTxPkt class */
public class ASTFCmdTxPkt extends ASTFCmd {
  private static final String NAME = "tx_msg";

  private String base64Buf;
  private int bufLen;
  private boolean isDict = false;

  /**
   * constructor
   *
   * @param asciiBuf
   */
  public ASTFCmdTxPkt(byte[] asciiBuf) {
    this(asciiBuf, 0, null);
  }

  /**
   * constructor
   *
   * @param asciiBuf
   * @param size
   * @param fill
   */
  public ASTFCmdTxPkt(byte[] asciiBuf, int size, byte[] fill) {
    super();
    String bufStr = encodeBase64(asciiBuf);
    base64Buf = bufStr;
    fields.addProperty("name", NAME);
    fields.addProperty("buf_index", -1);
    if (size > asciiBuf.length) {
      this.base64Buf = "{ \"base\": \"" + bufStr + "\", \"size\": " + size + " }";
      if (fill != null) {
        this.base64Buf =
            "{ \"base\": \""
                + bufStr
                + "\", \"fill\": \""
                + encodeBase64(fill)
                + "\", \"size\": "
                + size
                + " }";
      }
      this.bufLen = size;
      isDict = true;
    }
    stream = false;
    buffer = true;
  }

  public boolean isDict() {
    return isDict;
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
   * @return base64Buf
   */
  public String getBase64Buf() {
    return base64Buf;
  }

  /**
   * get buf_index
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
  public void setBufIndex(int index) {
    fields.addProperty("buf_index", index);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
