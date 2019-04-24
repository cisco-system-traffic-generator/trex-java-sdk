package com.cisco.trex.stateful;

import com.google.gson.JsonObject;

/**
 * Abstract Astf Cmd class
 */
public abstract class AstfCmd {

    protected JsonObject fields;
    protected Boolean stream = null;
    protected Boolean buffer = null;

    /**
     * construct
     */
    public AstfCmd() {
        fields = new JsonObject();
    }

    /**
     * get AstfCmd name
     *
     * @return Astf cmd name
     */
    public abstract String getName();

    /**
     * isStream
     *
     * @return true if it's stream
     */
    public Boolean isStream() {
        return stream;
    }

    /**
     * to json format
     *
     * @return JsonObject
     */
    public JsonObject toJson() {
        return fields;
    }

    /**
     * isBuffer
     *
     * @return true if it's buffer
     */
    public Boolean isBuffer() {
        return buffer;
    }

}


