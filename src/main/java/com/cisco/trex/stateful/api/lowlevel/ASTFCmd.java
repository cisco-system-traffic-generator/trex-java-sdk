package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;
import java.util.Base64;

/** Java implementation for TRex python sdk ASTFCmd class, the abstract Astf Cmd class, */
public abstract class ASTFCmd {

  protected JsonObject fields;
  protected Boolean stream = null;
  protected Boolean buffer = null;

  /** construct */
  public ASTFCmd() {
    fields = new JsonObject();
  }

  /**
   * get AstfCmd name
   *
   * @return Astf cmd name
   */
  public abstract String getName();

  /**
   * isStream
   *
   * @return true if it's stream
   */
  public Boolean isStream() {
    return stream;
  }

  /**
   * to json format
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    return fields;
  }

  /**
   * isBuffer
   *
   * @return true if it's buffer
   */
  public Boolean isBuffer() {
    return buffer;
  }

  public static String encodeBase64(byte[] bytes) {
    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(bytes);
  }
}
