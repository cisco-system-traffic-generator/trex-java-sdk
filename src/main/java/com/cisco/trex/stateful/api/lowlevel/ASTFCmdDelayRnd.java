package com.cisco.trex.stateful.api.lowlevel;

/**
 * Java implementation for TRex python sdk ASTFCmdDelayRnd class
 */
public class ASTFCmdDelayRnd extends ASTFCmd {
    private static final String NAME = "delay_rnd";

    /**
     * construct
     *
     * @param minSec minimum delay seconds
     * @param maxSec maximum delay seconds
     */
    public ASTFCmdDelayRnd(int minSec, int maxSec) {
        super();
        fields.addProperty("name", NAME);
        fields.addProperty("min_usec", minSec);
        fields.addProperty("max_usex", maxSec);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
