package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  Socket socket;

  public Client() throws IOException {
    
    Scanner scanner = new Scanner(System.in);
    String command;
    
    while (true) {
      System.out.print("Command: ");
      command = scanner.nextLine();
      
      if (command.equals(Common.EXIT_CODE)) break;
      
      if (command.equals(Common.PUT_FILE_CODE)) {
        System.out.print("Path: ");
        command = scanner.nextLine();
        
        connect();
        put(new File(command));
      }
      
      if (command.equals(Common.GET_FILE_CODE)) {
        System.out.print("Filename: ");
        command = scanner.nextLine();
        
        connect();
        get(command);
      }
      
      if (command.equals(Common.GET_LIST_CODE)) {
        connect();
        getList();
      }
    }
    
    scanner.close();
  }

  private void connect() throws IOException {
    socket = new Socket(Common.HOST, Common.PORT);
  }

  private void put(File file) throws IOException {
    if (file.exists()) {

      try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
          InputStream fis = new FileInputStream(file);) {       
        System.out.println("i/o streams has been created...");
        
        out.writeUTF(Common.PUT_FILE_CODE);
        out.writeUTF(file.getName());
        
        byte[] buffer = new byte[8192];
        int edge;

        System.out.println("Bytes are ready to be transmitted...");
        while (fis.available() > 0) {
          edge = fis.read(buffer);
          out.write(buffer, 0, edge);
        }
      }
      System.out.println("==Client== file length: " + file.length());
    } else {
      System.out.println("File does not exist...");
    }
  }
  
  private void get(String filename) throws IOException {
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());) {     
      System.out.println("i/o streams has been created...");
      
      out.writeUTF(Common.GET_FILE_CODE);
      out.writeUTF(filename);
      
      String response = in.readUTF();
      if (response.equals(Common.OK_STATUS)) {
        File file = new File(Common.PATH_TO_CLIENT_STORAGE + filename);
        if (file.exists()) file.delete();
        file.createNewFile();
        
        OutputStream fos = new FileOutputStream(file);
        
        byte[] buffer = new byte[8192];
        int edge;
        
        System.out.println("Bytes are ready to be recieved...");
        while ((edge = in.read(buffer)) != -1) {
          fos.write(buffer, 0, edge);
        }
        
        fos.close();
        
        System.out.println("==Client== file length: " + file.length());     
      } else {
        System.out.println("There is no such file in the server storage...");
      }
    }
  }
  
  private void getList() throws IOException {
    try (DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {
      System.out.println("i/o streams has been created...");
      
      out.writeUTF(Common.GET_LIST_CODE);
      String response = in.readUTF();
      if (response.equals("")) {
        System.out.println("There are no files in the server storage...");
      } else {
        System.out.println(response);
      }
    }
  }
  
  public static void main(String[] args) {
    try {
      new Client();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}