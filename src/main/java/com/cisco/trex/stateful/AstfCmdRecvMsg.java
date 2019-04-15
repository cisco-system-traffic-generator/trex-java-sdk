package com.cisco.trex.stateful;

/**
 * Astf Cmd Recv Msg
 */
public class AstfCmdRecvMsg extends AstfCmd {
    private static final String NAME = "tx_msg";

    /**
     * construct
     *
     * @param minPkts
     * @param clear
     */
    public AstfCmdRecvMsg(int minPkts, boolean clear) {
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
