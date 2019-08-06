package com.cisco.trex.stateful.api.lowlevel;

/**
 * Java implementation for TRex python sdk ASTFCmdNoClose class
 */
public class ASTFCmdNoClose extends ASTFCmd {
    private static final String NAME = "nc";

    /**
     * construct
     */
    public ASTFCmdNoClose() {
        fields.addProperty("name", NAME);
        stream = true;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
