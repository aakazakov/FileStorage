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
      
      if (command.equals(Common.PUT_FILE_CODE)) {
        System.out.print("Enter path: ");
        file = scanner.nextLine();
        put(file);
      }
      
      if (command.equals(Common.GET_FILE_CODE)) {
        System.out.print("Enter filename: ");
        file = scanner.nextLine();
        get(file);
      }
      
      if (command.equals(Common.GET_LIST_CODE)) {
        getList();
      }
    }   
    scanner.close();
  }
  
  private void put(String src) throws IOException {
    File file = new File(src);
    if (file.exists()) {
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
    } else {
      System.out.println("file not found by path: " + src);
    } 
  }
  
  private void get(String filename) throws IOException {
    out.writeUTF(Common.GET_FILE_CODE);
    out.writeUTF(filename);
    String response = in.readUTF();
    if (response.equals(Common.OK_STATUS)) {
      File file = new File(Common.PATH_TO_CLIENT_STORAGE + filename);
      if (file.exists()) file.delete();
      file.createNewFile();
      
      try (FileOutputStream fos = new FileOutputStream(file)) {
        byte[] buffer = new byte[8192];
        int edge;
        while (in.available() > 0) {
          edge = in.read(buffer);
          fos.write(buffer, 0, edge);
        }
        System.out.println("File in the client storage, file length: " + file.length());
      }
      
    } else {
      System.out.println("There is no such file in the server storage: " + filename);
    }
  }
  
  private void getList() throws IOException {
    out.writeUTF(Common.GET_LIST_CODE);
    String list = in.readUTF();
    System.out.println(list);
  }
}