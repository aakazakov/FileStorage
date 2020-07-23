package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.net.ServerSocket;

public class Server {
  DataInputStream in;
  DataOutputStream out;
  
  public Server() {
    try (ServerSocket server = new ServerSocket(Common.PORT)) {
      System.out.println("Server launched.");

      while (true) {
        Socket socket = server.accept();
        System.out.println("Client accepted.");
        
        in = new DataInputStream(socket.getInputStream());
        System.out.println("input stream...");
        
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("output stream...");
        
        interraction(socket);          
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new Server();
  }
  
  private void interraction(Socket socket) throws IOException {
    System.out.println("== interraction ==");
    String query;
    try {
      while (!socket.isClosed()) {
       System.out.println("server is waiting for query...");
       byte[] buffer = new byte[8192];
       int edge = in.read(buffer);
       query = new String(buffer, 0, edge);
       controller(query);
      }
    } catch (SocketException e) {
      // TODO SocketException handling.
      System.out.println("== interraction terminated ==");
    }   
  }
  
  private void controller(String query) { // TODO ArrayIndexOfBoundException...
    String[] arr = query.split(Common.DELIMETER);
    String command = arr[0];
    if (command.equals(Common.EXIT_CODE)) {
      //TODO Exit
    } else if (command.equals(Common.PUT_CODE)) {
      put(arr[1]);
    } else if (command.equals(Common.GET_LIST_CODE)) {
      getList();
    } else if (command.equals(Common.GET_FILE_CODE)) {
      getFile(arr[1]);
    } else {
      System.out.println("The server does not understand the client's query");
    }
  }
  
  private void put(String fileName) {
    FileOutputStream fos;
    try {
      File file = new File(Common.PATH_TO_STORAGE + fileName);
      if (file.exists()) { file.delete(); }
      file.createNewFile();
      fos = new FileOutputStream(file);
      byte[] buffer = new byte[8192];
      int edge;
      while ((edge = in.read(buffer)) != -1) {
        fos.write(buffer, 0, edge);
      }
      fos.close();
      System.out.println("File " + fileName + " uploaded.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void getList() {
    System.out.println("list");
  }
  
  private void getFile(String path) {
    System.out.println("file: " + path);
  }
}
