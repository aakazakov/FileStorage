package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;

public class Client {
  Socket socket;
  
  public Client() {
    socket = null;
    connect();
  }

  public static void main(String[] args) {
    
    Client client = new Client();
    client.showListOfFiles();
    
       
    
    client.showListOfFiles();
    
  }
  
  public void connect() {
    try {
      socket = new Socket(Common.HOST, Common.PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void disConnect() {
    if (socket == null || socket.isClosed()) { return; }
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void put(File file) {
    try (InputStream is = new FileInputStream(file);
         DataOutputStream os = new DataOutputStream(socket.getOutputStream());) {
      byte[] buffer = new byte[8192];
      int read;
      while ((read = is.read(buffer)) != -1) {
        os.write(buffer, 0, read);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void get(String fileName) {
    
  }
  
  public void showListOfFiles() {
    System.out.println("list");
  }
}
