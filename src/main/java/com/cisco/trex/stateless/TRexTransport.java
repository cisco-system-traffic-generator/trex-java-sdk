package com.cisco.trex.stateless;

import com.cisco.trex.stateless.model.RPCResponse;
import com.cisco.trex.stateless.util.IDataCompressor;
import com.cisco.trex.stateless.util.TRexDataCompressor;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import tools.jackson.databind.ObjectMapper;

/** TRex Transport class to create zmq socket for connection to trex server */
public class TRexTransport {

  private static final Logger LOGGER = LoggerFactory.getLogger(TRexTransport.class);
  private static final int DEFAULT_TIMEOUT = 3000;
  private static final String PROTOCOL = "tcp";
  private final String connectionString;
  private final IDataCompressor dataCompressor;
  private final ZMQ.Context zmqCtx = ZMQ.context(1);
  private final ZMQ.Socket zmqSocket;
  private final String host;
  private final String port;

  /**
   * @param host Server address
   * @param port Server port
   * @param timeout How long to wait for server response
   * @param dataCompressor
   */
  public TRexTransport(String host, String port, int timeout, IDataCompressor dataCompressor) {
    this.host = host;
    this.port = port;
    this.zmqSocket = zmqCtx.socket(SocketType.REQ);
    int actualTimeout = timeout <= 0 ? DEFAULT_TIMEOUT : timeout;
    zmqSocket.setReceiveTimeOut(actualTimeout);
    zmqSocket.setSendTimeOut(actualTimeout);
    zmqSocket.setHeartbeatIvl(10 * 60 * 1000);
    zmqSocket.setHeartbeatTimeout(60000);
    zmqSocket.setReconnectIVL(20);
    zmqSocket.setReconnectIVLMax(500);
    zmqSocket.setTCPKeepAlive(1);
    this.connectionString = PROTOCOL + "://" + this.host + ":" + this.port;
    zmqSocket.connect(connectionString);
    this.dataCompressor = dataCompressor;
  }

  /**
   * @param host Server address
   * @param port Server port
   * @param timeout How long to wait for server response
   */
  public TRexTransport(String host, String port, int timeout) {
    this(host, port, timeout, new TRexDataCompressor());
  }

  /**
   * Send TRex command to the server
   *
   * @param command
   * @return RPCResponse
   * @throws IOException
   */
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

  /**
   * Send TRex command list to the server
   *
   * @param commands
   * @return RPCResponse
   * @throws IOException
   */
  public RPCResponse[] sendCommands(List<TRexCommand> commands) throws IOException {
    if (commands.size() == 1) {
      return new RPCResponse[] {sendCommand(commands.get(0))};
    }

    List<Map<String, Object>> commandList =
        commands.stream().map(TRexCommand::getParameters).collect(Collectors.toList());

    if (commandList.isEmpty()) {
      return new RPCResponse[0];
    }

    String json = new ObjectMapper().writeValueAsString(commandList);
    String response = sendJson(json);
    return new ObjectMapper().readValue(response, RPCResponse[].class);
  }

  /** @return server host address */
  public String getHost() {
    return host;
  }

  /** @return server port */
  public String getPort() {
    return port;
  }

  /** close zmq connection to server */
  public synchronized void close() {
    zmqSocket.disconnect(connectionString);
    zmqSocket.close();
    zmqCtx.close();
  }

  /** @return zmq socket */
  public ZMQ.Socket getSocket() {
    return zmqSocket;
  }

  /**
   * Send json string to server
   *
   * @param json
   * @return json string
   */
  public synchronized String sendJson(String json) {
    LOGGER.debug("JSON Req: {}", json);

    byte[] compressed = this.dataCompressor.compressStringToBytes(json);

    String response;
    try {
      zmqSocket.send(compressed);
      byte[] msg = zmqSocket.recv();
      response = this.dataCompressor.decompressBytesToString(msg);
    } catch (ZMQException e) {
      throw new IllegalStateException(
          "Failed to send or recv json request or response due to ZMQ error: " + e.getErrorCode(),
          e);
    }

    if (response == null) {
      throw new IllegalStateException(
          "Got null json response, the reason could be get no response from server within timeout, or the ZMQ socket connection is in bad state either on client side or on server side.");
    }
    LOGGER.debug("JSON Resp: {}", response);
    return response;
  }
}
