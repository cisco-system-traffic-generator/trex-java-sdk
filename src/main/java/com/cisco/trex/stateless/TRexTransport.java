package com.cisco.trex.stateless;

import com.cisco.trex.stateless.model.RPCResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TRexTransport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TRexTransport.class);
    
    public static final int DEFAULT_RECEIVE_TIMEOUT = 3000;
    
    private final String connectionString;

    private ZMQ.Context zmqCtx = ZMQ.context(1);

    private ZMQ.Socket zmqSocket;

    private String protocol = "tcp";

    private String host;

    private String port;

    public TRexTransport(String host, String port, int receiveTimeout) {
        this.host = host;
        this.port = port;
        zmqSocket = zmqCtx.socket(ZMQ.REQ);
        int timeout = receiveTimeout == 0 ? DEFAULT_RECEIVE_TIMEOUT : receiveTimeout;
        zmqSocket.setReceiveTimeOut(timeout);
        connectionString = protocol + "://"+ this.host +":" + this.port;
        zmqSocket.connect(connectionString);
    }

    public RPCResponse sendCommand(TRexCommand command) throws IOException {
        String json = new ObjectMapper().writeValueAsString(command.getParameters());
        RPCResponse[] rpcResult = doSend(json);
        return rpcResult[0];
    }

    public RPCResponse[] sendCommands(List<TRexCommand> commands) throws IOException {
        List<Map<String, Object>> commandList = commands.stream().map(TRexCommand::getParameters).collect(Collectors.toList());
        
        if (commandList.isEmpty()) {
            return new RPCResponse[0];
        }
        
        String json = new ObjectMapper().writeValueAsString(commandList);
        return doSend(json);
    }

    synchronized private RPCResponse[] doSend(String json) throws IOException {
        LOGGER.info("JSON Req: " + json);
        zmqSocket.send(json);
        byte[] msg = zmqSocket.recv(0);
        if (msg == null) {
            int errNumber = zmqSocket.base().errno();
            throw new ZMQException("Unable to receive message from socket", errNumber);
        }
        String response = new String(msg);
        LOGGER.info("JSON Resp: " + response);
        
        return new ObjectMapper().readValue(response, RPCResponse[].class);
    }
    
    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    synchronized public void close() {
        zmqSocket.disconnect(connectionString);
        zmqCtx.close();
    }

    public ZMQ.Socket getSocket() {
        return zmqSocket;
    }

    synchronized public byte[] sendJson(String json) {
        zmqSocket.send(json);
        return zmqSocket.recv(0);
    }
}
