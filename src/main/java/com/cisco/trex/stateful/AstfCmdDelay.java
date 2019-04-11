package com.cisco.trex.stateful;

/**
 * Astf Cmd Delay
 */
public class AstfCmdDelay extends AstfCmd  {
    private static final String NAME="delay";

    /**
     * construct
     * @param usec
     */
    public AstfCmdDelay(int usec){
        super();
        fields.addProperty("name", NAME);
        fields.addProperty("usec", usec);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
