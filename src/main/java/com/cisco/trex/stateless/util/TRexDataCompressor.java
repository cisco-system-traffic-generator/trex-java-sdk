package com.cisco.trex.stateless.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** TRex Data Compressor */
public class TRexDataCompressor implements IDataCompressor {
  // Magic bytes from TrexRpcZip
  private static final byte[] TREX_HEADER_MAGIC =
      new byte[] {(byte) 0xAB, (byte) 0xE8, (byte) 0x5C, (byte) 0xEA};
  private static final int HEADER_SIZE = 8; // 4 magic bytes and 4 bytes Integer (request length)
  private static final Logger LOGGER = LoggerFactory.getLogger(TRexDataCompressor.class);

  @Override
  public byte[] compressStringToBytes(String request) {
    // prepare compression header
    ByteBuffer headerByteBuffer = ByteBuffer.allocate(HEADER_SIZE);
    headerByteBuffer.put(TREX_HEADER_MAGIC);
    headerByteBuffer.putInt(request.length());
    byte[] headerBytes = headerByteBuffer.array();

    // compress request
    byte[] compressedRequest = compressBytes(request.getBytes());

    return concatByteArrays(headerBytes, compressedRequest);
  }

  @Override
  public String decompressBytesToString(byte[] data) {
    if (data == null) {
      return null;
    }

    if (data.length > HEADER_SIZE) {

      byte[] magicBytes = Arrays.copyOfRange(data, 0, TREX_HEADER_MAGIC.length);
      if (Arrays.equals(magicBytes, TREX_HEADER_MAGIC)) {

        // Skip another 4 bytes containing the uncompressed size of the message
        byte[] compressedData = Arrays.copyOfRange(data, HEADER_SIZE, data.length);
        try {
          return new String(decompressBytes(compressedData));
        } catch (DataFormatException ex) {
          LOGGER.error("Header is correct, but unable to decompress data", ex);
        }
      }
    }

    return new String(data);
  }

  private static byte[] concatByteArrays(byte[] firstDataArray, byte[] secondDataArray) {
    byte[] concatedDataArray = new byte[firstDataArray.length + secondDataArray.length];
    System.arraycopy(firstDataArray, 0, concatedDataArray, 0, firstDataArray.length);
    System.arraycopy(
        secondDataArray, 0, concatedDataArray, firstDataArray.length, secondDataArray.length);
    return concatedDataArray;
  }

  private static byte[] compressBytes(byte[] data) {
    Deflater deflater = new Deflater();
    deflater.setInput(data);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    deflater.finish();
    byte[] buffer = new byte[1024];

    while (!deflater.finished()) {
      int count = deflater.deflate(buffer);
      outputStream.write(buffer, 0, count);
    }

    return outputStream.toByteArray();
  }

  private static byte[] decompressBytes(byte[] data) throws DataFormatException {
    Inflater inflater = new Inflater();
    inflater.setInput(data);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] buffer = new byte[1024];
    while (!inflater.finished()) {
      int count = inflater.inflate(buffer);
      outputStream.write(buffer, 0, count);
    }

    return outputStream.toByteArray();
  }
}
