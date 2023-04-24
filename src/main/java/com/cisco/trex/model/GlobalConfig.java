package com.cisco.trex.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/** Global configuration parameters */
public class GlobalConfig extends HashMap<String, Object> {

    public boolean getProcessAtCp() {
        return (boolean) get("process_at_cp");
    }

    public double getSchedMaxStretch() {
        return (double) get("sched_max_stretch");
    }
}
