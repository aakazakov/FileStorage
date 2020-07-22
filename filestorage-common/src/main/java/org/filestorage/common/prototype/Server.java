package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
  DataInputStream in;
  // DataOutputStream out;
  
  public Server() {
    try (ServerSocket server = new ServerSocket(Common.PORT)) {
      System.out.println("Server launched.");
      Socket socket = server.accept();
      System.out.println("Client accepted.");
      in = new DataInputStream(socket.getInputStream());
      // out = new DataOutputStream(socket.getOutputStream());
      
      byte[] buffer = new byte[8192];
      int edge;
      
      String fileName = in.readUTF();
      File file = new File(fileName);
      if (file.exists()) file.delete();
      file.createNewFile();
      System.out.println("File " + file.getName() + " created.");
      
      FileOutputStream fos = new FileOutputStream(file);
      while ((edge = in.read(buffer)) != -1) {
        System.out.println("server edge: " + edge);
        fos.write(buffer, 0, edge);
      }      
      fos.close();
      System.out.println("File " + file.getAbsolutePath() +  " uploaded.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    new Server();
  }
}
