package com.cisco.trex.stateless;

import com.cisco.trex.stateless.model.RPCResponse;
import com.cisco.trex.stateless.util.IDataCompressor;
import com.cisco.trex.stateless.util.TRexDataCompressor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TRexTransport {

    private static final Logger LOGGER = LoggerFactory.getLogger(TRexTransport.class);
    
    public static final int DEFAULT_TIMEOUT = 3000;
    
    private final String connectionString;

    private final IDataCompressor dataCompressor;

    private ZMQ.Context zmqCtx = ZMQ.context(1);

    private ZMQ.Socket zmqSocket;

    private String protocol = "tcp";

    private String host;

    private String port;

    public TRexTransport(String host, String port, int timeout, IDataCompressor dataCompressor) {
        this.host = host;
        this.port = port;
        zmqSocket = zmqCtx.socket(ZMQ.REQ);
        int actualTimeout = timeout <= 0 ? DEFAULT_TIMEOUT : timeout;
        zmqSocket.setReceiveTimeOut(actualTimeout);
        zmqSocket.setSendTimeOut(actualTimeout);
        connectionString = protocol + "://"+ this.host +":" + this.port;
        zmqSocket.connect(connectionString);
        this.dataCompressor = dataCompressor;
    }

    public TRexTransport(String host, String port, int timeout) {
        this(host, port, timeout, new TRexDataCompressor());
    }

    public RPCResponse sendCommand(TRexCommand command) throws IOException {
        String json = new ObjectMapper().writeValueAsString(command.getParameters());
        String response = sendJson(json);
        ObjectMapper objectMapper = new ObjectMapper();
        if (objectMapper.readTree(response).isArray()) {
            // for versions of TRex before v2.61, single entry response also wrapped with json array
            RPCResponse[] rpcResult = objectMapper.readValue(response, RPCResponse[].class);
            return rpcResult[0];
        }
        return objectMapper.readValue(response, RPCResponse.class);
    }

    public RPCResponse[] sendCommands(List<TRexCommand> commands) throws IOException {
        if (commands.size() ==1) {
            return new RPCResponse[] {sendCommand(commands.get(0))};
        }

        List<Map<String, Object>> commandList = commands.stream().map(TRexCommand::getParameters).collect(Collectors.toList());
        
        if (commandList.isEmpty()) {
            return new RPCResponse[0];
        }
        
        String json = new ObjectMapper().writeValueAsString(commandList);
        String response = sendJson(json);
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
        zmqSocket.close();
        zmqCtx.close();
    }

    public ZMQ.Socket getSocket() {
        return zmqSocket;
    }

    synchronized public String sendJson(String json) {
        LOGGER.debug("JSON Req: " + json);

        byte[] compressed = this.dataCompressor.compressStringToBytes(json);

        zmqSocket.send(compressed);
        byte[] msg = zmqSocket.recv();

        String response = this.dataCompressor.decompressBytesToString(msg);
        LOGGER.debug("JSON Resp: " + response);
        return response;
    }
}
