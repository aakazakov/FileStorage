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
  
  private void connect() throws IOException {
    socket = new Socket(Common.HOST, Common.PORT);
  }
  
  private void interaction() {
    Scanner scanner = new Scanner(System.in);
    StringBuilder query = new StringBuilder();
    while (true) {
      query.setLength(0);
      
      System.out.print("Enter the command: ");
      String command = scanner.nextLine();
      if (command.equals(Common.EXIT_CODE)) break;
      
      query.append(command).append(Common.DELIMETER);
      
      if (command.equals(Common.GET_FILE_CODE)) {
        System.out.print("Enter filename: ");
        query.append(scanner.nextLine());
      }
      
      send(query.toString());
      
      if (command.equals(Common.PUT_CODE)) {
        System.out.print("Enter filename: ");
        query.append(scanner.nextLine());
        System.out.print("Enter path: ");
        put(scanner.nextLine());
      }
    }   
    scanner.close();
  }
  
  private void send(String query) {
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
      out.writeUTF(query);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void put(String src) {
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
      File file = new File(src);
      FileInputStream fis = new FileInputStream(file);
      byte[] buffer = new byte[8192];
      int edge;
      while (fis.available() > 0) {
        edge = fis.read(buffer);
        out.write(buffer, 0, edge);
      }
      out.flush();
      fis.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void get(String fileName) {
    System.out.println(fileName);
  }
  
  private void getList() {
    
  }
}