package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/** Java implementation for TRex python sdk ASTFAssociationRule class */
public class ASTFAssociationRule {
  private JsonObject fields = new JsonObject();
  private int port;

  /**
   * construct
   *
   * @param ipStart
   * @param ipEnd
   * @param port
   */
  public ASTFAssociationRule(String ipStart, String ipEnd, int port) {
    this.port = port;
    fields.addProperty("port", port);
    if (!StringUtils.isEmpty(ipStart)) {
      fields.addProperty("ip_start", ipStart);
    }
    if (!StringUtils.isEmpty(ipStart)) {
      fields.addProperty("ip_end", ipEnd);
    }
  }

  /**
   * construct
   *
   * @param port
   */
  public ASTFAssociationRule(int port) {
    this(null, null, port);
  }

  /**
   * get port
   *
   * @return port
   */
  public int getPort() {
    return this.port;
  }

  /**
   * to json format
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    return fields;
  }
}
