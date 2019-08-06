package com.cisco.trex.stateful.api.lowlevel;

/**
 * Java implementation for TRex python sdk ASTFCmdRecvMsg class
 */
public class ASTFCmdRecvMsg extends ASTFCmd {
    private static final String NAME = "tx_msg";

    /**
     * construct
     *
     * @param minPkts
     * @param clear
     */
    public ASTFCmdRecvMsg(int minPkts, boolean clear) {
        super();
        fields.addProperty("name", NAME);
        fields.addProperty("min_pkts", minPkts);
        if (clear) {
            fields.addProperty("clear", "true");
        }
        stream = false;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
