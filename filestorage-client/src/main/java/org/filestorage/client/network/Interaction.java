package org.filestorage.client.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import org.filestorage.common.Config;
import org.filestorage.common.Constants;
import org.filestorage.common.Utility;

public class Interaction {

  private Socket socket;
  
  public void put(File file) throws IOException {
    connect();
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        FileInputStream fis = new FileInputStream(file);) {
      
      byte[] buffer = new byte[512];
      int edge;
      
      System.out.println("Writing start...");
      
      out.writeByte(Constants.PUT);
      System.out.println(in.readByte());
      out.write(file.getName().getBytes());
      System.out.println(in.readByte());
      out.write(Utility.longToBytes(file.length()));
      System.out.println(in.readByte());
      
      while (fis.available() > 0) {
        edge = fis.read(buffer);
        out.write(buffer, 0, edge);
      }
      
      System.out.println(in.readByte() == Constants.PUT);
            
      System.out.println("Writing finish...");
    }
    disconnect();
  }
    
  private void connect() throws IOException {
    socket = new Socket(Config.HOST, Config.PORT);
    System.out.println("Сlient connected to server...");
  }
  
  private void disconnect() throws IOException {
    socket.close();
    System.out.println("Connection terminated...");
  }  
}
