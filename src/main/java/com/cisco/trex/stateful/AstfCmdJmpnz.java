package com.cisco.trex.stateful;

/**
 * Astf Cmd Jmp nz
 */
public class AstfCmdJmpnz extends AstfCmd{
    private static final String NAME="jmp_nz";

    private String label;

    /**
     * construct
     * @param idVal
     * @param offset
     * @param label
     */
    public AstfCmdJmpnz(String idVal, int offset, String label){
        super();
        this.label=label;
        fields.addProperty("name", NAME);
        fields.addProperty("id", idVal);
        fields.addProperty("offset", offset);
    }

    @Override
    public boolean isStream() {
        return super.isStream();
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * get label
     * @return label
     */
    public String getLabel(){
        return label;
    }
}
