package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;

/** Java implementation for TRex python sdk ASTFTemplateCache class */
public class ASTFTemplateCache {
  BufferList programs = new BufferList();

  public ASTFTemplateCache() {}

  public void clearCache() {
    programs = new BufferList();
  }

  public JsonArray toJson() {
    JsonArray jsonArray = new JsonArray();

    for (Object obj : programs.getBufList()) {
      ASTFProgram program = (ASTFProgram) obj;
      jsonArray.add(program.toJson());
    }
    return jsonArray;
  }

  public long getTotalSendBytes(int index) {
    ASTFProgram program = (ASTFProgram) programs.getBufList().get(index);
    return program.getTotalSendBytes();
  }

  public int getNumPrograms() {
    return programs.getLen();
  }

  public void addProgramFromTemplates(ASTFTemplateBase template) {
    try {
      template.setProgramIndex(programs.add(template.getProgram()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
