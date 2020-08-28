package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;
import java.util.HashMap;
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
      Map<String, List<Integer>> l7Map = new HashMap<>();
      l7Map.put("offset", l7List);
      fields.addProperty("l7_map", String.valueOf(l7Map));
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
      fields.addProperty("l7_map", String.valueOf(l7Map));
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
