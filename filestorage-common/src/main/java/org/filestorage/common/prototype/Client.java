package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  Socket socket;
  DataOutputStream out;
  
  public Client() throws IOException {
    socket = new Socket(Common.HOST, Common.PORT);
    out = new DataOutputStream(socket.getOutputStream());
    interraction();
  }
  
  private void interraction() throws IOException {
    Scanner scanner = new Scanner(System.in);
    String query = "";
    while (!query.equals("/q")) {
      System.out.println("Query: ");
      query = scanner.nextLine();
      send(query);
    }
  }
  
  private void send(String query) throws IOException {
    out.write(query.getBytes());
  }
  
  public static void main(String[] args) {
    try {
      new Client();
    } catch (IOException e) {
      e.printStackTrace();
    }       
  }
  
  private void put(File file) {
    try (InputStream in = new FileInputStream(file)) {     
      byte[] buffer = new byte[8192];
      int edge;
      
      out.writeUTF(file.getName());
      
      while ((edge = in.read(buffer)) != -1) {
        out.write(buffer, 0, edge);
      }         
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
