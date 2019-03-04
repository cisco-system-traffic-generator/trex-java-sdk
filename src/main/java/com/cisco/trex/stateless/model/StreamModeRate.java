package com.cisco.trex.stateless.model;

import java.util.Objects;

public class StreamModeRate {
    
    private Type type;
    
    private Double value;

    public StreamModeRate(Type type, Double value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public Double getValue() {
        return value;
    }

    public enum Type {
        pps("pps"), bps_L1("bps_L1"), bps_L2("bps_L2"), percentage("percentage");
        String name;

        Type(String name) {
            this.name = name;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof  StreamModeRate)) {
            return false;
        }

        StreamModeRate rhs = (StreamModeRate) obj;

        return Objects.equals(this.value, rhs.value) &&
                Objects.equals(this.type, rhs.type);
    }
}
