package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/** Java implementation for TRex python sdk ASTFAssociationRule class */
public class ASTFAssociationRule {

  private JsonObject fields = new JsonObject();
  private int port;

  /**
   * constructor
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

  /** @param port */
  public ASTFAssociationRule(int port) {
    this(null, null, port);
  }

  /**
   * constructor
   *
   * @param ipStart
   * @param ipEnd
   * @param port
   * @param l7List
   */
  public ASTFAssociationRule(String ipStart, String ipEnd, int port, List<Integer> l7List) {
    this(null, null, port);
    if (!l7List.isEmpty()) {
      fields.addProperty("l7_map", "{\"offset\": " + l7List.toString() + "}");
    }
  }

  /**
   * construct
   *
   * @param ipStart
   * @param ipEnd
   * @param port
   * @param l7Map
   */
  public ASTFAssociationRule(
      String ipStart, String ipEnd, int port, Map<String, List<Integer>> l7Map) {
    this(null, null, port);
    if (!l7Map.isEmpty()) {
      String l7_map =
          String.format(
              "{\"offset\": %s, \"value\": %s}",
              l7Map.get("offset").toString(), l7Map.get("value").toString());
      fields.addProperty("l7_map", l7_map);
    }
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
