package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class ServerSide {
  DataInputStream is;
  DataOutputStream os;
  
  public ServerSide() {
    start();
  }

  public static void main(String[] args) {
    new ServerSide();
  }
  
  public void start() {
    try (ServerSocket server = new ServerSocket(Common.PORT)) {
      System.out.println("Server started");
      Socket socket = server.accept();
      System.out.println("Client accepted!");
//      is = new DataInputStream(socket.getInputStream());
//      os = new DataOutputStream(socket.getOutputStream());
//      String fileName = is.readUTF();
//      File file = new File("./common/prototype/" + fileName);
//      file.createNewFile();
//      byte[] buffer = new byte[8192];
//      int read;
//      while ((read = is.read()) != -1) {
//        os.write(buffer, 0, read);
//      }
//      System.out.println("File " + fileName + " uploaded.");      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
