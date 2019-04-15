package com.cisco.trex.stateful;

/**
 * Astf Cmd Keepa live Msg
 */
public class AstfCmdKeepaliveMsg extends AstfCmd {
    private static final String NAME = "keepalive";

    /**
     * construct
     *
     * @param msec
     */
    public AstfCmdKeepaliveMsg(int msec) {
        super();
        fields.addProperty("name", NAME);
        fields.addProperty("msec", msec);
        stream = false;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
