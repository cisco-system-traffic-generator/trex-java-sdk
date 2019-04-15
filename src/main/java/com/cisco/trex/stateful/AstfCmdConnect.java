package com.cisco.trex.stateful;

/**
 * Astf Cmd Connect
 */
public class AstfCmdConnect extends AstfCmd {
    private static final String NAME = "connect";

    /**
     * construct
     */
    public AstfCmdConnect() {
        fields.addProperty("name", NAME);
        stream = true;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
