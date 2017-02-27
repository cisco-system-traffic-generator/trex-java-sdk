package com.cisco.trex.stateless.model;

import com.google.gson.JsonObject;

public class PortStatus {
    public Integer max_stream_id;
    public Boolean service;
    public String owner;
    public String state;
    public Integer fc;
    public Boolean linkUp;
    public Boolean multicast;
    public Boolean promiscuous;
    public Boolean grat_arp;
    public Boolean latency;
    public Boolean queue;
    public String rx_filter_mode;
    public Integer speed;
    public LayerConfigurationMode cfgMode;

    public PortStatus(Integer max_stream_id, Boolean service, String owner, String state, Integer fc, Boolean linkUp, Boolean multicast, Boolean promiscuous, Boolean grat_arp, Boolean latency, Boolean queue, String rx_filter_mode, Integer speed, JsonObject jsonObject) {
        this.max_stream_id = max_stream_id;
        this.service = service;
        this.owner = owner;
        this.state = state;
        this.fc = fc;
        this.linkUp = linkUp;
        this.multicast = multicast;
        this.promiscuous = promiscuous;
        this.grat_arp = grat_arp;
        this.latency = latency;
        this.queue = queue;
        this.rx_filter_mode = rx_filter_mode;
        this.speed = speed;
        this.cfgMode = new LayerConfigurationMode(jsonObject);
    }

    public static PortStatus fromJson(JsonObject jsonObject) {
        JsonObject attr = jsonObject.getAsJsonObject("attr");
        JsonObject rx_info = jsonObject.getAsJsonObject("rx_info");
        return new PortStatus(
            jsonObject.get("max_stream_id").getAsInt(),
            jsonObject.get("service").getAsBoolean(),
            jsonObject.get("owner").getAsString(),
            jsonObject.get("state").getAsString(),
            attr.getAsJsonObject("fc").get("mode").getAsInt(),
            attr.getAsJsonObject("link").get("up").getAsBoolean(),
            attr.getAsJsonObject("multicast").get("enabled").getAsBoolean(),
            attr.getAsJsonObject("promiscuous").get("enabled").getAsBoolean(),
            rx_info.getAsJsonObject("grat_arp").get("is_active").getAsBoolean(),
            rx_info.getAsJsonObject("latency").get("is_active").getAsBoolean(),
            rx_info.getAsJsonObject("queue").get("is_active").getAsBoolean(),
            attr.get("rx_filter_mode").getAsString(),
            attr.get("speed").getAsInt(),
            attr.getAsJsonObject("layer_cfg")
        );
    }

    //    public PortStatusAttribute.link getLink() {
//        return attr.link;
//    }

    class PortStatusAttribute {
        
        public fc fc;
        public link link;
        public promiscuous promiscuous;

        public PortStatusAttribute(PortStatusAttribute.fc fc, PortStatusAttribute.link link, PortStatusAttribute.promiscuous promiscuous) {
            this.fc = fc;
            this.link = link;
            this.promiscuous = promiscuous;
        }

        class fc {
            public Integer mode;

            public fc(Integer mode) {
                this.mode = mode;
            }
        }
        class link {
            public Boolean up;

            public link(Boolean up) {
                this.up = up;
            }
        }
        class promiscuous {
            public Boolean enabled;

            public promiscuous(Boolean enabled) {
                this.enabled = enabled;
            }
        }
    }
}
