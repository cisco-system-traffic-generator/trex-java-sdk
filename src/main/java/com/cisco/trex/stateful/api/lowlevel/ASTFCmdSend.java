package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdSend class */
public class ASTFCmdSend extends ASTFCmd {
  private static final String NAME = "tx";

  private String base64Buf;
  private int bufLen;
  private int index;
  private boolean isDict = false;

  public boolean isDict() {
    return isDict;
  }

  /**
   * constructor
   *
   * @param asciiBuf
   */
  public ASTFCmdSend(byte[] asciiBuf) {
    this(asciiBuf, 0, null);
  }

  /**
   * constructor
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
    this.index = -1;
    bufLen = asciiBuf.length;
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
    stream = true;
    buffer = true;
  }

  /**
   * get buf length
   *
   * @return buf Len
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
   * get buf index
   *
   * @return index
   */
  public int getBufIndex() {
    return index;
  }

  /**
   * set buf index
   *
   * @param index
   */
  public void setBufIndex(int index) {
    fields.addProperty("buf_index", index);
    this.index = index;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
