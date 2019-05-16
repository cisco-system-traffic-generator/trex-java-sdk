package com.cisco.trex.stateful;

/**
 * Astf Cmd Delay Rnd
 */
public class AstfCmdDelayRnd extends AstfCmd {
    private static final String NAME = "delay_rnd";

    /**
     * construct
     *
     * @param minSec minimum delay seconds
     * @param maxSec maximum delay seconds
     */
    public AstfCmdDelayRnd(int minSec, int maxSec) {
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
