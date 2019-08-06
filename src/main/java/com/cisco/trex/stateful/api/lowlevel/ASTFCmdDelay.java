package com.cisco.trex.stateful.api.lowlevel;

/**
 * Java implementation for TRex python sdk ASTFCmdDelay class
 */
public class ASTFCmdDelay extends ASTFCmd {
    private static final String NAME = "delay";

    /**
     * construct
     *
     * @param usec
     */
    public ASTFCmdDelay(int usec) {
        super();
        fields.addProperty("name", NAME);
        fields.addProperty("usec", usec);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
