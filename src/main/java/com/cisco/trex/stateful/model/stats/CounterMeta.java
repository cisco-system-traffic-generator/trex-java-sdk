package com.cisco.trex.stateful.model.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CounterMeta {

    @JsonProperty("abs")
    public boolean isAbs;

    @JsonProperty("help")
    public String help;

    @JsonProperty("id")
    public int id;

    @JsonProperty("info")
    public String info;

    @JsonProperty("name")
    public String name;

    @JsonProperty("real")
    public boolean isReal;

    @JsonProperty("units")
    public String units;

    @JsonProperty("zero")
    public boolean isZero;

    @JsonProperty("abs")
    public boolean getAbs() {
        return isAbs;
    }

    @JsonProperty("abs")
    public void setAbs(boolean abs) {
        this.isAbs = abs;
    }

    @JsonProperty("help")
    public String getHelp() {
        return help;
    }

    @JsonProperty("help")
    public void setHelp(String help) {
        this.help = help;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("info")
    public String getInfo() {
        return info;
    }

    @JsonProperty("info")
    public void setInfo(String info) {
        this.info = info;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("real")
    public boolean isReal() {
        return isReal;
    }

    @JsonProperty("real")
    public void setReal(boolean isReal) {
        this.isReal = isReal;
    }

    @JsonProperty("units")
    public String getUnits() {
        return units;
    }

    @JsonProperty("units")
    public void setUnits(String units) {
        this.units = units;
    }

    @JsonProperty("zero")
    public boolean isZero() {
        return isZero;
    }

    @JsonProperty("zero")
    public void setZero(boolean isZero) {
        this.isZero = isZero;
    }

}
