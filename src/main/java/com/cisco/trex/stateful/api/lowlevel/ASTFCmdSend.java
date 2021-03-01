package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFCmdSend class */
public class ASTFCmdSend extends ASTFCmd {
  private static final String NAME = "tx";

  private String buf;
  private int bufLen;

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
    this.buf = bufStr;
    fields.addProperty("name", NAME);
    fields.addProperty("buf_index", -1);
    this.bufLen = asciiBuf.length;
    if (size > asciiBuf.length) {
      this.buf = "{ \"base\": \"" + bufStr + "\", \"size\": " + size + " }";
      if (fill != null) {
        this.buf =
            "{ \"base\": \""
                + bufStr
                + "\", \"fill\": \""
                + encodeBase64(fill)
                + "\", \"size\": "
                + size
                + " }";
      }
      this.bufLen = size;
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
   * @return encoded base64 buf
   */
  public String buf() {
    return buf;
  }

  /**
   * get buf index
   *
   * @return index
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
