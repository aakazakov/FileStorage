package org.filestorage.common;

import java.lang.reflect.Field;

public class Constants {
  
  public static final byte PUT = -127;
  public static final byte GET = -126;
  public static final byte GET_LIST = -125;
  public static final byte START = -124;
  public static final byte END = -123;
  public static final byte FAIL = -122;
  public static final byte REMOVE = -121;
  
  public static String getConstantName(byte value) {
    Class<Constants> constants = Constants.class;
    Field[] fields = constants.getFields();
    for (Field f : fields) {
      try {
        if (f.getByte(constants) == value) {
          return f.getName();
        }
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return " ";
  }
  
  private Constants() { }
}
