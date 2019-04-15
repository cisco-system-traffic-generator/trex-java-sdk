package com.cisco.trex.stateful;

/**
 * Astf Cmd No Close
 */
public class AstfCmdNoClose extends AstfCmd {
    private static final String NAME = "nc";

    /**
     * construct
     */
    public AstfCmdNoClose() {
        fields.addProperty("name", NAME);
        stream = true;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
