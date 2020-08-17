package org.filestorage.common;

public class Utility {
  
  public static byte[] longToBytes(long num) {
    byte[] bytes = new byte[Long.BYTES + 1];
    for (int i = 7; i >= 0; i--) {
      bytes[i] = (byte) (num & 0xff);
      num >>= 8;
    }
    return bytes;
  }
  
  public static long bytesToLong(byte[] bytes) {
    long num = 0;
    for (int i = 0; i < 8; i++) {
      num <<= 8;
      num |= (bytes[i] & 0xFF);
    }
    return num;
  }
}
