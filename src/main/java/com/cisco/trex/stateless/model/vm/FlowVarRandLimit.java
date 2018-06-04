package com.cisco.trex.stateless.model.vm;

public class FlowVarRandLimit extends VMInstruction {

    private String type;
    private String name;
    private int size;
    private int limit;
    private long seed;
    private long min_value;
    private long max_value;

    public FlowVarRandLimit(String name, int size, int limit, long seed, long min_value, long max_value) {
        super();
        this.type = "flow_var_rand_limit";
        this.name = name;
        this.size = size;
        this.limit = limit;
        this.seed = seed;
        this.min_value = min_value;
        this.max_value = max_value;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getLimit() {
        return limit;
    }

    public long getSeed() {
        return seed;
    }

    public long getMin_value() {
        return min_value;
    }

    public long getMax_value() {
        return max_value;
    }

}
