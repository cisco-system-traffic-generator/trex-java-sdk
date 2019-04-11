package com.cisco.trex.stateful;

/**
 * Astf Cmd Set Val
 */
public class AstfCmdSetVal extends AstfCmd {
    private static final String NAME="set_var";

    /**
     * construct
     * @param idVal
     * @param val
     */
    public AstfCmdSetVal(String idVal,int val){
        super();
        this.fields.addProperty("name", NAME);
        this.fields.addProperty("id", idVal);
        this.fields.addProperty("val", val);
    }

    @Override
    public boolean isStream() {
        return super.isStream();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
