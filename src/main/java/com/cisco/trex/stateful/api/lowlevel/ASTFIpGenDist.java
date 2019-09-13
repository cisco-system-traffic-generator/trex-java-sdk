package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Java implementation for TRex python sdk ASTFIpGenDist class */
public class ASTFIpGenDist {
  private static List<Inner> inList = new ArrayList<>();
  private Inner newInner;
  private int index;

  /**
   * construct
   *
   * @param ipStart
   * @param ipEnd
   */
  public ASTFIpGenDist(String ipStart, String ipEnd) {
    this(ipStart, ipEnd, Distribution.SEQ, null);
  }

  /**
   * construct
   *
   * @param ipStart
   * @param ipEnd
   * @param distribution
   * @param perCoreDistributionVals
   */
  public ASTFIpGenDist(
      String ipStart,
      String ipEnd,
      Distribution distribution,
      PerCoreDistributionVals perCoreDistributionVals) {
    this.newInner = new Inner(ipStart, ipEnd, distribution, perCoreDistributionVals);

    for (int i = 0; i < inList.size(); i++) {
      if (newInner.equals(inList.get(i))) {
        this.index = i;
        return;
      }
    }
    ASTFIpGenDist.inList.add(newInner);
    this.index = inList.size() - 1;
  }

  /**
   * getIpStart
   *
   * @return ip start
   */
  public String getIpStart() {
    return ASTFIpGenDist.inList.get(this.index).getIpStart();
  }

  /**
   * getIpEnd
   *
   * @return ip end
   */
  public String getIpEnd() {
    return ASTFIpGenDist.inList.get(this.index).getIpEnd();
  }

  /**
   * getDistribution
   *
   * @return distribution
   */
  public Distribution getDistribution() {
    return ASTFIpGenDist.inList.get(this.index).getDistribution();
  }

  /**
   * getPerCoreDistributionVals
   *
   * @return perCoreDistributionVals
   */
  public PerCoreDistributionVals getPerCoreDistributionVals() {
    return ASTFIpGenDist.inList.get(this.index).getPerCoreDistributionVals();
  }

  /**
   * setDirection
   *
   * @param direction direction
   */
  public void setDirection(String direction) {
    ASTFIpGenDist.inList.get(this.index).setDirection(direction);
  }

  /**
   * setIpOffset
   *
   * @param ipOffset ipOffset
   */
  public void setIpOffset(String ipOffset) {
    ASTFIpGenDist.inList.get(this.index).setIpOffset(ipOffset);
  }

  /**
   * to json format
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("index", this.index);
    return jsonObject;
  }

  /**
   * including all cached gen dist json string
   *
   * @return JsonArray
   */
  public static JsonArray clssToJson() {
    JsonArray jsonArray = new JsonArray();
    for (Inner inner : inList) {
      jsonArray.add(inner.toJson());
    }
    return jsonArray;
  }

  /** class reset, clear all cached data */
  public static void classReset() {
    inList.clear();
  }

  /** Inner class */
  class Inner {
    private String ipStart;
    private String ipEnd;
    private Distribution distribution;
    private PerCoreDistributionVals perCoreDistributionVals;
    private JsonObject fields = new JsonObject();

    /**
     * Inner Construct
     *
     * @param ipStart
     * @param ipEnd
     * @param distribution
     * @param perCoreDistributionVals
     */
    Inner(
        String ipStart,
        String ipEnd,
        Distribution distribution,
        PerCoreDistributionVals perCoreDistributionVals) {
      fields.addProperty("ip_start", ipStart);
      fields.addProperty("ip_end", ipEnd);
      fields.addProperty("distribution", distribution.getType());
      if (perCoreDistributionVals != null) {
        fields.addProperty("per_core_distribution", perCoreDistributionVals.getType());
      }
    }

    String getIpStart() {
      return ipStart;
    }

    String getIpEnd() {
      return ipEnd;
    }

    Distribution getDistribution() {
      return distribution;
    }

    PerCoreDistributionVals getPerCoreDistributionVals() {
      return perCoreDistributionVals;
    }

    void setDirection(String direction) {
      fields.addProperty("dir", direction);
    }

    void setIpOffset(String ipOffset) {
      fields.addProperty("ip_offset", ipOffset);
    }

    JsonObject toJson() {
      return fields;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Inner inner = (Inner) o;
      return fields.equals(inner.fields);
    }

    @Override
    public int hashCode() {
      return Objects.hash(fields);
    }
  }

  /** Distribution enum */
  public enum Distribution {
    SEQ("seq"),
    RAND("rand");

    private String type;

    Distribution(String type) {
      this.type = type;
    }

    /**
     * get type
     *
     * @return type
     */
    public String getType() {
      return type;
    }
  }

  /** PerCoreDistributionVals enum */
  public enum PerCoreDistributionVals {
    DEFAULT("default"),
    SEQ("seq");
    String type;

    PerCoreDistributionVals(String type) {
      this.type = type;
    }

    /**
     * get type
     *
     * @return type
     */
    public String getType() {
      return type;
    }
  }
}
