package com.cisco.trex.stateful;

/**
 * Astf Cmd Reset
 */
public class AstfCmdReset extends AstfCmd {
    private static final String NAME = "reset";

    /**
     * construct
     */
    public AstfCmdReset() {
        fields.addProperty("name", NAME);
        stream = true;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
