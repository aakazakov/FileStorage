package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  Socket socket;
  DataInputStream in;
  DataOutputStream out;
  
  public Client() throws IOException {
    connect();
    interaction();
  }
  
  public static void main(String[] args) {
    try {
      new Client();
    } catch (IOException e) {
      e.printStackTrace();
    }
  } 
  
  private void connect() throws IOException {
    socket = new Socket(Common.HOST, Common.PORT);
    out = new DataOutputStream(socket.getOutputStream());
    in = new DataInputStream(socket.getInputStream());
  }
  
  private void interaction() throws IOException {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print("Enter the command: ");
      String command = scanner.nextLine();
      
      if (command.equals(Common.EXIT_CODE)) break;
      
      String file;
      
      if (command.equals(Common.GET_FILE_CODE)) {
        System.out.print("Enter filename: ");
        file = scanner.nextLine();
        get(file);
      }
      
      if (command.equals(Common.PUT_FILE_CODE)) {
        System.out.print("Enter path: ");
        file = scanner.nextLine();
        put(file);
      }
      
      if (command.equals(Common.GET_LIST_CODE)) {
        System.out.println("files list");
      }
    }   
    scanner.close();
  }
  
  private void put(String src) throws IOException {
    File file = new File(src);
    out.writeUTF(Common.PUT_FILE_CODE);
    out.writeUTF(file.getName());
    
    try (FileInputStream fis = new FileInputStream(file)) {
      byte[] buffer = new byte[8192];
      int edge;
      while (fis.available() > 0) {
        edge = fis.read(buffer);
        out.write(buffer, 0, edge);
      }
    }
  }
  
  private void get(String fileName) {

  }
  
  private void getList() {
    
  }
}