package com.cisco.trex.stateful;

/**
 * Astf Cmd Receive class
 */
public class AstfCmdRecv extends AstfCmd {
    private static final String NAME="rx";

    /**
     * construct
     * @param minBytes minimal receive bytes
     * @param clear true if clear data
     */
    public AstfCmdRecv(int minBytes,boolean clear){
        super();
        fields.addProperty("name", NAME);
        fields.addProperty("min_bytes",minBytes);
        if(clear){
            fields.addProperty("clear", "true");
        }
        stream=true;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
