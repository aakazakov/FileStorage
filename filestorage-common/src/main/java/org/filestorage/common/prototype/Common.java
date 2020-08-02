package org.filestorage.common.prototype;

/** different stuff) */
public class Common { 
  public static final String HOST = "127.0.0.1";
  public static final int PORT = 8189;
  
  public static final String EXIT_CODE = "/q";
  public static final String PUT_FILE_CODE = "/put";
  public static final String GET_FILE_CODE = "/getfile";
  public static final String GET_LIST_CODE = "/getlist";
  public static final String OK_STATUS = "OK";
  public static final String FAIL_STATUS = "FAIL";
  
  public static final String PATH_TO_SERVER_STORAGE = "src/main/java/org/filestorage/common/prototype/server_storage/";
  public static final String PATH_TO_CLIENT_STORAGE = "src/main/java/org/filestorage/common/prototype/client_storage/";
  
  private Common() {
    
  }
  
  public static byte[] longToBytes(long num) {
    byte[] bytes = new byte[Long.BYTES];
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
