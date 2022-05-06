package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
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
    this(ipStart, ipEnd, port);
    if (!l7List.isEmpty()) {
      JsonObject l7_map = new JsonObject();
      addL7MapProperty(l7List, l7_map, "offset");
      fields.add("l7_map", l7_map);
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
    this(ipStart, ipEnd, port);
    if (!l7Map.isEmpty()) {
      JsonObject l7_map = new JsonObject();
      addL7MapProperty(l7Map.get("offset"), l7_map, "offset");
      addL7MapProperty(l7Map.get("value"), l7_map, "value");
      fields.add("l7_map", l7_map);
    }
  }

  private void addL7MapProperty(List<Integer> values, JsonObject l7Map, String property) {
    if (values == null || values.isEmpty()) {
      return;
    }
    JsonArray jsonArray = new JsonArray();
    values.forEach(jsonArray::add);
    l7Map.add(property, jsonArray);
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
