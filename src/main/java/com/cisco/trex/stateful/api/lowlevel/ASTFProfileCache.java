package com.cisco.trex.stateful.api.lowlevel;

/** Java implementation for TRex python sdk ASTFProfileCache class */
public class ASTFProfileCache {
  private ASTFIPGenDistCache genDistCache;
  private ASTFProgramCache programCache;
  private ASTFTemplateCache templateCache;
  private ASTFProfile profile;

  public ASTFProfileCache(ASTFProfile profile) {
    this.profile = profile;
    genDistCache = new ASTFIPGenDistCache();
    programCache = new ASTFProgramCache();
    templateCache = new ASTFTemplateCache();
  }

  public void clearAll() {
    genDistCache = new ASTFIPGenDistCache();
    programCache = new ASTFProgramCache();
    templateCache = new ASTFTemplateCache();
  }

  public void fillCache() {
    clearAll();
    if (profile.getAstfTemplateList() == null || profile.getAstfTemplateList().isEmpty()) {
      return;
    }
    for (ASTFTemplate template : profile.getAstfTemplateList()) {
      ASTFTCPClientTemplate astfTcpClientTemplate = template.getAstfTcpClientTemplate();
      ASTFTCPServerTemplate astfTcpServerTemplate = template.getAstfTcpServerTemplate();
      programCache.addCommandsFromProgram(astfTcpClientTemplate.getProgram());
      programCache.addCommandsFromProgram(astfTcpServerTemplate.getProgram());
      templateCache.addProgramFromTemplates(astfTcpClientTemplate);
      templateCache.addProgramFromTemplates(astfTcpServerTemplate);
      genDistCache.addInner(astfTcpClientTemplate.getIpGen());
    }
  }

  public ASTFProgramCache getProgramCache() {
    return programCache;
  }

  public ASTFIPGenDistCache getGenDistCache() {
    return genDistCache;
  }

  public ASTFTemplateCache getTemplateCache() {
    return templateCache;
  }
}
