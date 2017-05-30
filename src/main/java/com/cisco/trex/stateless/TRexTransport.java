package com.cisco.trex.stateless;

import com.cisco.trex.stateless.model.RPCResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import java.io.IOException;

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

    synchronized public RPCResponse sendCommand(TRexCommand command) throws IOException {

        String json = new ObjectMapper().writeValueAsString(command.getParameters());

        LOGGER.info("JSON Req: " + json);
        zmqSocket.send(json);
        byte[] msg = zmqSocket.recv(0);
        if (msg == null) {
            int errNumber = zmqSocket.base().errno();
            throw new ZMQException("Unable to receive message from socket", errNumber);
        }
        String response = new String(msg);
        LOGGER.info("JSON Resp: " + response);

        RPCResponse[] rpcResult = new ObjectMapper().readValue(response, RPCResponse[].class);
        return rpcResult[0];
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
