package com.cisco.trex.stateless.util;

public interface IDataCompressor {
  byte[] compressStringToBytes(String request);

  String decompressBytesToString(byte[] data);
}
