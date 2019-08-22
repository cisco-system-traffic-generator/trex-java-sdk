package com.cisco.trex.stateless.model.vm;

public enum VariableOperation {
  INC("inc"),
  DEC("dec"),
  RANDOM("random");

  String op;

  VariableOperation(String op) {
    this.op = op;
  }

  String getValue() {
    return op;
  }
}
