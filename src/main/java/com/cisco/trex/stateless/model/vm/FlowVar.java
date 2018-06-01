package com.cisco.trex.stateless.model.vm;

import java.util.List;

public class FlowVar extends VMInstruction {

    private String type;
    private String name;
    private int size;
    private String op;
    private long init_value;
    private long min_value;
    private long max_value;
    private long step = 1;
    private List<Long> value_list;

    public FlowVar(String name, int size, VariableOperation op, long init_value, long min_value, long max_value,
            long step) {
        super();
        this.type = "flow_var";
        this.name = name;
        this.size = size;
        this.op = op.getValue();
        this.init_value = init_value;
        this.min_value = min_value;
        this.max_value = max_value;
        this.step = step;
    }

    public FlowVar(String name, int size, VariableOperation op, List<Long> value_list) {
        super();
        this.type = "flow_var";
        this.name = name;
        this.size = size;
        this.op = op.getValue();
        this.value_list = value_list;
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

    public String getOp() {
        return op;
    }

    public long getInit_value() {
        return init_value;
    }

    public long getMin_value() {
        return min_value;
    }

    public long getMax_value() {
        return max_value;
    }

    public long getStep() {
        return step;
    }

    public List<Long> getValueList() {
        return value_list;
    }

}
