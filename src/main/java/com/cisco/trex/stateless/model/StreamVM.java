package com.cisco.trex.stateless.model;

import com.cisco.trex.stateless.model.vm.VMInstruction;
import java.util.List;
import java.util.Objects;

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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (!(obj instanceof StreamVM)) {
      return false;
    }

    StreamVM rhs = (StreamVM) obj;

    return Objects.equals(this.split_by_var, rhs.split_by_var)
        && Objects.equals(this.instructions, rhs.instructions);
  }
}
