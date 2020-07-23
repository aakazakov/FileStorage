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
    StringBuilder query = new StringBuilder();
    String command;
    while (true) {
      query.setLength(0);
      System.out.print("Command: ");
      command = scanner.nextLine();
      if (command.equals("/q")) { break; }
      System.out.print("Source (if need or press Enter): ");
      query.append(command).append(Common.DELIMETER).append(scanner.nextLine());
      send(query.toString());
    }
    scanner.close();
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
