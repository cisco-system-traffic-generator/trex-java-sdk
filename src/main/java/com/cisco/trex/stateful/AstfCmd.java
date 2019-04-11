package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

/**
 * Abstract Astf Cmd class
 */
public abstract class AstfCmd {

    protected JsonObject fields;
    protected boolean stream;
    protected boolean buffer;

    /**
     * construct
     */
    public AstfCmd(){
        fields=new JsonObject();
        stream=false;
        buffer=false;
    }

    /**
     * get AstfCmd name
     * @return Astf cmd name
     */
    public abstract String getName();

    /**
     * isStream
     * @return true if it's stream
     */
    public boolean isStream(){
        return stream;
    }

    /**
     * to json format
     * @return JsonObject
     */
    public JsonObject toJson(){
        return fields;
    }

    /**
     * isBuffer
     * @return true if it's buffer
     */
    public boolean isBuffer(){
        return buffer;
    }
}


