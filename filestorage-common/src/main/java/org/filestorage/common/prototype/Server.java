package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
  public Server() {
    try (ServerSocket server = new ServerSocket(Common.PORT)) {
      System.out.println("Server launched.");

      while (true) {
        Socket socket = server.accept();
        new ClientHandler(server, socket);
        System.out.println("Client accepted.");
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new Server();
  }
}
