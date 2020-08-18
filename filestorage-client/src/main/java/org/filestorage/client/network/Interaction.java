package org.filestorage.client.network;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import org.filestorage.common.Config;
import org.filestorage.common.Constants;

public class Interaction {

  private Socket socket;
  
  public void put(File file) throws IOException {
    connect();
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
      
      System.out.println(file.getName());
      System.out.println(file.length());
      
      System.out.println("Writing start...");
      
      out.write(Constants.PUT);
      
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
