package org.filestorage.client.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import org.filestorage.common.Config;

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
      
      while (fis.available() > 0) {
        edge = fis.read(buffer);
        out.write(buffer, 0, edge);
      }
      
      System.out.println(in.readByte());
            
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
