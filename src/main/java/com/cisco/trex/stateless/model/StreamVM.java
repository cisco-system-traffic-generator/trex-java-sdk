package com.cisco.trex.stateless.model;

import java.util.List;

import com.cisco.trex.stateless.model.vm.VMInstruction;

public class StreamVM {
    private String split_by_var;
    private List<VMInstruction> instructions;

    public StreamVM(String split_by_var, List<VMInstruction> instructions) {
        this.split_by_var = split_by_var;
        this.instructions = instructions;
    }

    public String getSplit_by_var() {
        return split_by_var;
    }

    public List<VMInstruction> getInstructions() {
        return instructions;
    }
}
