package com.cisco.trex.stateful;

/**
 * Astf Cmd Tx Mode
 */
public class AstfCmdTxMode extends AstfCmd {
    private static final String NAME = "tx_mode";

    /**
     * construct
     *
     * @param flags
     */
    public AstfCmdTxMode(int flags) {
        fields.addProperty("name", NAME);
        fields.addProperty("flags", flags);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
