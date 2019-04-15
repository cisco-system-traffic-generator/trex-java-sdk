package com.cisco.trex.stateful;

/**
 * Astf Cmd Close Msg
 */
public class AstfCmdCloseMsg extends AstfCmd {
    private static final String NAME = "close_msg";

    /**
     * construct
     */
    public AstfCmdCloseMsg() {
        super();
        fields.addProperty("name", NAME);
        stream = false;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
