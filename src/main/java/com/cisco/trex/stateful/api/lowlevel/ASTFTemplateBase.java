package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/**
 * Java implementation for TRex python sdk _ASTFTemplateBase class
 *
 * <p>abstract AstfTemplateBase class
 */
abstract class ASTFTemplateBase {
  JsonObject fields = new JsonObject();
  private boolean isStream;
  private ASTFProgram program;

  public ASTFTemplateBase(ASTFProgram program) {
    this.fields.addProperty("program_index", -1);
    this.program = program;
    this.isStream = program.isStream();
  }

  /**
   * isStream
   *
   * @return true if is stream
   */
  public boolean isStream() {
    return isStream;
  }

  /**
   * to json format
   *
   * @return json object
   */
  public JsonObject toJson() {
    return fields;
  }

  public int getProgramIndex() {
    return this.fields.get("program_index").getAsInt();
  }

  public void setProgramIndex(int index) {
    this.fields.addProperty("program_index", index);
  }

  public ASTFProgram getProgram() {
    return program;
  }
}
