package com.cisco.trex.stateful.api.lowlevel;

/**
 * Java implementation for TRex python sdk ASTFCmdTxMode class
 */
public class ASTFCmdTxMode extends ASTFCmd {
    private static final String NAME = "tx_mode";

    /**
     * construct
     *
     * @param flags
     */
    public ASTFCmdTxMode(int flags) {
        fields.addProperty("name", NAME);
        fields.addProperty("flags", flags);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
