package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
  ServerSocket server;
  DataInputStream in;
  DataOutputStream out;
  
  public Server() throws IOException {
    start();
  }
  
  private void start() throws IOException {
    server = new ServerSocket(Common.PORT);
    System.out.println("Server has been started...");
    
    Socket socket = server.accept();
    System.out.println("Client has been accepted...");
    
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
    System.out.println("i/o streams has been created...");
    
    String filename = in.readUTF();
    
    File file = new File(Common.PATH_TO_SERVER_STORAGE + filename);
    if (file.exists()) file.delete();
    file.createNewFile();
    
    try (OutputStream fos = new FileOutputStream(file)) {
      byte[] buffer = new byte[8192];
      int edge;
      
      System.out.println("Bytes are ready to be recieved...");
      while ((edge = in.read(buffer)) != -1) {
        fos.write(buffer, 0, edge);
      }
      
      System.out.println("==Server== file length: " + file.length());
    }
  }
  
  @SuppressWarnings("unused")
  private void stop() throws IOException {
    server.close();
    System.out.println("Server has been stopped...");
  }
  
  public static void main(String[] args) {
    try {
      new Server();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
