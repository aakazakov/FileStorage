package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  Socket socket;
  
  public Client() {
    interaction();
  }
  
  public static void main(String[] args) {
    new Client();
  } 
  
  public void connect() throws IOException {
    socket = new Socket(Common.HOST, Common.PORT);
  }
  
  public void interaction() {
    Scanner scanner = new Scanner(System.in);
    StringBuilder query = new StringBuilder();
    while (true) {
      query.setLength(0);
      
      System.out.print("Enter the command: ");
      String command = scanner.nextLine();
      if (command.equals("/q")) break;
      query.append(command).append(Common.DELIMETER);
      
      System.out.print("Enter filename (if needed or press `Enter`): ");
      query.append(scanner.nextLine());
      
      send(query.toString());
    }   
    scanner.close();
  }
  
  public void send(String query) {
    System.out.println("Command: " + query);
  }
}