package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Java implementation for TRex python sdk ASTFIpGenDist class */
public class ASTFIpGenDist {

  private Inner inner;
  private int index;

  public ASTFIpGenDist(String ipStart, String ipEnd) {
    this(ipStart, ipEnd, Distribution.SEQ, null);
  }

  public ASTFIpGenDist(String ipStart, String ipEnd, Distribution distribution) {
    this(ipStart, ipEnd, distribution, null);
  }

  public ASTFIpGenDist(
      String ipStart,
      String ipEnd,
      Distribution distribution,
      PerCoreDistribution perCoreDistribution) {
    this.inner = new Inner(ipStart, ipEnd, distribution, perCoreDistribution);
    this.index = -1;
  }

  public void setInner(Inner inner) {
    this.inner = inner;
  }

  public Inner getInner() {
    return inner;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public String getIpStart() {
    return inner.getIpStart();
  }

  public String getIpEnd() {
    return inner.getIpEnd();
  }

  public Distribution getDistribution() {
    return inner.getDistribution();
  }

  public PerCoreDistribution getPerCoreDistribution() {
    return inner.getPerCoreDistribution();
  }

  public Direction getDirection() {
    return inner.getDirection();
  }

  public void setDirection(Direction direction) {
    inner.setDirection(direction);
  }

  public void setIpOffset(String ipOffset) {
    inner.setIpOffset(ipOffset);
  }

  public String getIpOffset() {
    return inner.getIpOffset();
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("index", index);
    return jsonObject;
  }
  /** Inner class */
  public class Inner {
    private JsonObject fields = new JsonObject();
    private String ipStart;
    private String ipEnd;
    private Distribution distribution;
    private PerCoreDistribution perCoreDistribution;
    private Direction direction;

    Inner(
        String ipStart,
        String ipEnd,
        Distribution distribution,
        PerCoreDistribution perCoreDistribution) {
      fields.addProperty("ip_start", ipStart);
      fields.addProperty("ip_end", ipEnd);
      fields.addProperty("distribution", distribution.getType());
      if (perCoreDistribution != null) {
        fields.addProperty("pre_core_distribution", perCoreDistribution.getType());
        this.perCoreDistribution = perCoreDistribution;
      }
      this.ipStart = ipStart;
      this.ipEnd = ipEnd;
      this.distribution = distribution;
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

    public boolean isOverLaps(Inner other) {
      int thisIpStart = ipToInt(getIpStart());
      int thisIpEnd = ipToInt(getIpEnd());
      int otherIpStart = ipToInt(other.getIpStart());
      int otherIpEnd = ipToInt(other.getIpEnd());
      return thisIpStart <= otherIpStart && thisIpEnd >= otherIpEnd;
    }

    private int ipToInt(String ipv4Addr) {
      if (!isIPv4Address(ipv4Addr)) throw new RuntimeException("Invalid ip address");

      Pattern pattern = Pattern.compile("\\d+");
      Matcher matcher = pattern.matcher(ipv4Addr);
      int result = 0;
      int counter = 0;
      while (matcher.find()) {
        int value = Integer.parseInt(matcher.group());
        result = (value << 8 * (3 - counter++)) | result;
      }
      return result;
    }

    private boolean isIPv4Address(String ipv4Addr) {
      String lower = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])";
      String regex = lower + "(\\." + lower + "){3}";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(ipv4Addr);
      return matcher.matches();
    }

    public String getIpStart() {
      return ipStart;
    }

    public String getIpEnd() {
      return ipEnd;
    }

    public Distribution getDistribution() {
      return distribution;
    }

    public PerCoreDistribution getPerCoreDistribution() {
      return perCoreDistribution;
    }

    public Direction getDirection() {
      return direction;
    }

    public void setDirection(Direction direction) {
      fields.addProperty("dir", direction.getDirection());
      this.direction = direction;
    }

    public String getIpOffset() {
      return fields.get("ip_offset").getAsString();
    }

    public void setIpOffset(String ipOffset) {
      fields.addProperty("ip_offset", ipOffset);
    }

    public JsonObject toJson() {
      return fields;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(fields);
    }
  }

  public enum Direction {
    CLIENT("c"),
    SERVER("s");
    private String direction;

    Direction(String direction) {
      this.direction = direction;
    }

    public String getDirection() {
      return direction;
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

    public String getType() {
      return type;
    }
  }

  /** PreCoreDistribution enum */
  public enum PerCoreDistribution {
    DEFAULT("default"),
    SEQ("seq");
    private String type;

    PerCoreDistribution(String type) {
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
