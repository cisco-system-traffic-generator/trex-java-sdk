package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java implementation for TRex python sdk _ASTFTemplateBase class
 * 
 * abstract AstfTemplateBase class
 */
abstract class ASTFTemplateBase {
    private static List<ASTFProgram> programList = new ArrayList();
    private static Map<ASTFProgram, Integer> programHash = new HashMap();

    private JsonObject fields = new JsonObject();
    private int programIndex;
    private boolean isStream;

    /**
     * construct
     *
     * @param astfProgram
     */
    public ASTFTemplateBase(ASTFProgram astfProgram) {
        this.isStream = astfProgram.isStream();
        fields.addProperty("program_index", addProgram(astfProgram));
    }

    /**
     * get Total Send Bytes
     *
     * @param index index of the astf program
     * @return astf program Total Send Bytes
     */
    public static int getTotalSendBytes(int index) {
        return programList.get(index).getTotalSendBytes();
    }

    /**
     * add astf Program
     *
     * @param astfProgram
     * @return program index in the program List
     */
    public static int addProgram(ASTFProgram astfProgram) {
        if (programHash.containsKey(astfProgram)) {
            return programHash.get(astfProgram);
        }
        programList.add(astfProgram);
        int index = programList.size() - 1;
        programHash.put(astfProgram, index);
        return index;
    }

    /**
     * isStream
     *
     * @return true if is stream
     */
    public boolean isStream() {
        return isStream;
    }

    /**
     * programList size
     *
     * @return size of the program list
     */
    public static int programNum() {
        return programList.size();
    }

    /**
     * to json format
     *
     * @return json object
     */
    public JsonObject toJson() {
        return fields;
    }

    /**
     * clear all cached data
     */
    public static void classReset() {
        programList.clear();
        programHash.clear();
    }

    /**
     * including all cached astf template json string
     *
     * @return JsonArray
     */
    public static JsonArray classToJson() {
        JsonArray jsonArray = new JsonArray();
        for (ASTFProgram astfProgram : programList) {
            jsonArray.add(astfProgram.toJson());
        }
        return jsonArray;
    }
}
