package com.cisco.trex.stateful;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Astf Ip Gen Dist
 */
public class AstfIpGenDist {
    private static List<Inner> inList = new ArrayList();
    private String ipStart;
    private String ipEnd;
    private Distribution distribution;
    private PerCoreDistributionVals perCoreDistributionVals;
    private Inner newInner;
    private int index;

    /**
     * construct
     * @param ipStart
     * @param ipEnd
     */
    public AstfIpGenDist(String ipStart, String ipEnd){
        this.ipStart=ipStart;
        this.ipEnd=ipEnd;
        this.distribution= Distribution.SEQ;
        this.perCoreDistributionVals=null;
        this.newInner=new Inner(ipStart,ipEnd,distribution,perCoreDistributionVals);
        for (int i=0;i<inList.size();i++){
            if (newInner.equals(inList.get(i))){
                this.index=i;
                return;
            }
        }
        this.inList.add(newInner);
        this.index= inList.size()-1;
    }

    /**
     * construct
     * @param ipStart
     * @param ipEnd
     * @param distribution
     * @param perCoreDistributionVals
     */
    public AstfIpGenDist(String ipStart,String ipEnd,Distribution distribution,PerCoreDistributionVals perCoreDistributionVals){
        this.ipStart=ipStart;
        this.ipEnd=ipEnd;
        this.distribution=distribution;
        this.perCoreDistributionVals=perCoreDistributionVals;
        this.newInner=new Inner(ipStart,ipEnd,distribution,perCoreDistributionVals);

        for (int i=0;i<inList.size();i++){
            if (newInner.equals(inList.get(i))){
                this.index=i;
                return;
            }
        }
        this.inList.add(newInner);
        this.index= inList.size()-1;
    }

    /**
     * getIpStart
     * @return ip start
     */
    public String getIpStart() {
        return this.inList.get(this.index).getIpStart();
    }

    /**
     * getIpEnd
     * @return ip end
     */
    public String getIpEnd() {
        return this.inList.get(this.index).getIpEnd();
    }

    /**
     * getDistribution
     * @return distribution
     */
    public Distribution getDistribution() {
        return this.inList.get(this.index).getDistribution();
    }

    /**
     * getPerCoreDistributionVals
     * @return perCoreDistributionVals
     */
    public PerCoreDistributionVals getPerCoreDistributionVals() {
        return this.inList.get(this.index).getPerCoreDistributionVals();
    }

    /**
     * setDirection
     * @param direction direction
     */
    public void setDirection(String direction) {
        this.inList.get(this.index).setDirection(direction);
    }

    /**
     * setIpOffset
     * @param ipOffset ipOffset
     */
    public void setIpOffset(String ipOffset) {
        this.inList.get(this.index).setIpOffset(ipOffset);
    }

    /**
     * to json format
     * @return json string
     */
    public JsonObject toJson(){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("index", this.index);
        return jsonObject;
    }

    /**
     * including all cached gen dist json string
     * @return json format
     */
    public static JsonArray clssToJson(){
        JsonArray jsonArray=new JsonArray();
        for (Inner inner:inList){
            jsonArray.add(inner.toJson());
        }
        return jsonArray;
    }

    /**
     * Inner class
     */
    class Inner{
        private String ipStart;
        private String ipEnd;
        private Distribution distribution;
        private PerCoreDistributionVals perCoreDistributionVals;

        private String direction;
        private String ipOffset;

        private JsonObject fields=new JsonObject();

        /**
         * Inner Construct
         * @param ipStart
         * @param ipEnd
         * @param distribution
         * @param perCoreDistributionVals
         */
        Inner(String ipStart,String ipEnd,Distribution distribution,PerCoreDistributionVals perCoreDistributionVals){
            fields.addProperty("ip_start", ipStart);
            fields.addProperty("ip_end", ipEnd);
            fields.addProperty("distribution", distribution.getType());
            if (perCoreDistributionVals!=null){
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
            this.direction = direction;
        }

        void setIpOffset(String ipOffset) {
            fields.addProperty("ip_offset", ipOffset);
            this.ipOffset = ipOffset;
        }

        JsonObject toJson(){
            return fields;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Inner inner = (Inner) o;
            return fields.equals(inner.fields);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fields);
        }
    }

    /**
     * Distribution enum
     */
    public enum Distribution {
        SEQ("seq"),
        RAND("rand");

        private String type;

        Distribution(String type) {
            this.type = type;
        }

        /**
         * get type
         * @return
         */
        public String getType() {
            return type;
        }
    }

    /**
     * PerCoreDistributionVals enum
     */
    public enum PerCoreDistributionVals {
        DEFAULT("default"),
        SEQ("seq");
        String type;

        PerCoreDistributionVals(String type) {
            this.type = type;
        }

        /**
         * get type
         * @return
         */
        public String getType() {
            return type;
        }
    }
}
