package com.cisco.trex.stateless.model.port;

import com.cisco.trex.stateless.model.LayerConfigurationMode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PortAttributes {
    @JsonProperty("fc")
    private PortFCAttribute flowControl;

    @JsonProperty("layer_cfg")
    private LayerConfigurationMode layerConiguration;

    @JsonProperty("link")
    private PortLinkAttribute link;

    @JsonProperty("promiscuous")
    private PortPromiscuousAttribute promiscuous;

    @JsonProperty("multicast")
    private PortMulticastAttribute multicast;

    @JsonProperty("rx_filter_mode")
    private String rx_filter_mode;

    @JsonProperty("speed")
    private int speed;

    @JsonProperty("fc")
    public PortFCAttribute getFlowControl() {
        return flowControl;
    }

    @JsonProperty("fc")
    public void setFlowControl(PortFCAttribute flowControl) {
        this.flowControl = flowControl;
    }

    @JsonProperty("layer_cfg")
    public LayerConfigurationMode getLayerConiguration() {
        return layerConiguration;
    }

    @JsonProperty("layer_cfg")
    public void setLayerConiguration(LayerConfigurationMode layerConiguration) {
        this.layerConiguration = layerConiguration;
    }

    @JsonProperty("link")
    public PortLinkAttribute getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(PortLinkAttribute link) {
        this.link = link;
    }

    @JsonProperty("promiscuous")
    public PortPromiscuousAttribute getPromiscuous() {
        return promiscuous;
    }

    @JsonProperty("promiscuous")
    public void setPromiscuous(PortPromiscuousAttribute promiscuous) {
        this.promiscuous = promiscuous;
    }

    @JsonProperty("multicast")
    public PortMulticastAttribute getMulticast() {
        return multicast;
    }

    @JsonProperty("multicast")
    public void setMulticast(PortMulticastAttribute multicast) {
        this.multicast = multicast;
    }

    @JsonProperty("rx_filter_mode")
    public String getRx_filter_mode() {
        return rx_filter_mode;
    }

    @JsonProperty("rx_filter_mode")
    public void setRx_filter_mode(String rx_filter_mode) {
        this.rx_filter_mode = rx_filter_mode;
    }

    @JsonProperty("speed")
    public int getSpeed() {
        return speed;
    }

    @JsonProperty("speed")
    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
