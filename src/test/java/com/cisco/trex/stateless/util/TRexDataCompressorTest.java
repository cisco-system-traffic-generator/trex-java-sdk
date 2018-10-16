package com.cisco.trex.stateless.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TRexDataCompressorTest {

    private TRexDataCompressor compressor;
    private String requestJsonString;
    private String responseJsonString;
    private byte[] requestCompressedBytes;
    private byte[] responseCompressedBytes;

    @Before
    public void setUp() {
        compressor = new TRexDataCompressor();

        requestJsonString = "{\"method\":\"api_sync\",\"id\":672327041,\"jsonrpc\":\"2.0\",\"params\":{\"api_h\":null,\"api_vers\":[{\"major\":4,\"minor\":0,\"type\":\"core\"}]}}";

        responseJsonString = "[{\"id\":672327041,\"jsonrpc\":\"2.0\",\"result\":{\"api_vers\":[{\"api_h\":\"0E1vxb39\",\"type\":\"core\"}]}}]\n";

        requestCompressedBytes = new byte[]{-85, -24, 92, -22, 0, 0, 0, 125, 120, -100, 29, -116, -37, 10, -125, 48, 16, 68, -1, 101, -98, 23, 73, -93, 84, -56, -81, -108, 82, 66, 26, 48, 98, 46, 108, 84, -112, -112, 127, -17, -42, -73, 57, -100, -103, 105, -120, 126, 95, -14, 23, 6, -74, -124, 79, -67, -110, 3, 33, 8, 63, 103, 61, -22, 89, 77, 15, -62, 90, 115, -30, -30, -92, -93, 7, 37, -70, 88, -74, -79, -62, -76, 123, -77, -64, -92, 99, -37, -24, -122, -45, -77, -120, 87, 67, -76, 107, 102, -104, -119, 16, 67, -6, 39, 69, -40, -81, -30, -27, -60, 101, -10, -24, -17, -34, 127, -120, -111, 39, -116};
        responseCompressedBytes = new byte[]{-85, -24, 92, -22, 0, 0, 0, 94, 120, -100, -117, -82, 86, -54, 76, 81, -78, 50, 51, 55, 50, 54, 50, 55, 48, 49, -44, 81, -54, 42, -50, -49, 43, 42, 72, 86, -78, 82, 50, -46, 51, 80, -46, 81, 42, 74, 45, 46, -51, 41, 81, -78, -86, 86, 74, 44, -56, -116, 47, 75, 45, 42, 86, -78, -118, -122, 112, 50, -128, -118, 12, 92, 13, -53, 42, -110, -116, 45, -127, 42, 75, 42, 11, 82, -127, 34, -55, -7, 69, -87, 74, -75, -79, -75, -75, -79, 92, 0, 31, 2, 28, -103};
    }

    @Test
    public void compressDecompress() {
        byte[] compressedRequest = compressor.compressStringToBytes(requestJsonString);

        String decompressedRequest = compressor.decompressBytesToString(compressedRequest);

        assertEquals(decompressedRequest, requestJsonString);

        byte[] compressedResponse = compressor.compressStringToBytes(responseJsonString);
        String decompressedResponse = compressor.decompressBytesToString(compressedResponse);

        assertEquals(decompressedResponse, responseJsonString);
    }


    @Test
    public void compressStringToBytes() {
        byte[] compressedRequest = compressor.compressStringToBytes(requestJsonString);

        assertArrayEquals(requestCompressedBytes, compressedRequest);

        byte[] compressedResponse = compressor.compressStringToBytes(responseJsonString);

        assertArrayEquals(compressedResponse, responseCompressedBytes);
    }

    @Test
    public void decompressBytesToString() {
        String decompressedRequest = compressor.decompressBytesToString(requestCompressedBytes);

        assertEquals(decompressedRequest, requestJsonString);

        String decompressedResponse = compressor.decompressBytesToString(responseCompressedBytes);

        assertEquals(decompressedResponse, responseJsonString);
    }
}