package com.cisco.trex.stateful;


import com.google.gson.JsonObject;

/**
 * Astf Global Info
 */
public class AstfGlobalInfo implements AstfGlobalInfoBase{
    private JsonObject scheduler=new JsonObject();
    private JsonObject ipv6=new JsonObject();
    private JsonObject tcp=new JsonObject();
    private JsonObject ip=new JsonObject();

    @Override
    public AstfGlobalInfo scheduler(SchedulerParam schedulerParam,int value){
        scheduler.addProperty(schedulerParam.getType(), value);
        return this;
    }

    @Override
    public AstfGlobalInfo ipv6(Ipv6Param ipv6Param,int value){
        ipv6.addProperty(ipv6Param.getType(), value);
        return this;
    }

    @Override
    public AstfGlobalInfo tcp(TcpParam tcpParam,int value){
        tcp.addProperty(tcpParam.getType(), value);
        return this;
    }

    @Override
    public AstfGlobalInfo ip(IpParam ipParam,int value){
        ip.addProperty(ipParam.getType(), value);
        return this;
    }

    @Override
    public JsonObject toJson(){
        JsonObject json=new JsonObject();
        if(scheduler.size()!=0){
            json.add("scheduler", this.scheduler);
        }
        if (ipv6.size()!=0){
            json.add("ipv6", this.ipv6);
        }
        if(tcp.size()!=0){
            json.add("tcp",this.tcp);
        }
        if(ip.size()!=0){
            json.add("ip", this.ip);
        }
        return json;
    }


}


