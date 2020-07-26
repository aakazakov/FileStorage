package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
  private class ClientRequestsHandler implements Runnable {
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    
    public ClientRequestsHandler(Socket socket) throws IOException {
      this.socket = socket;
      in = new DataInputStream(socket.getInputStream());
      out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
      System.out.println("Client in thread...");
      
      while (isRunning) {
        try {         
          String request = in.readUTF();
          
          if (request.equals(Common.PUT_FILE_CODE)) {
            request = in.readUTF();
            put(request);
          }
          
          if (request.equals(Common.GET_FILE_CODE)) {
            request = in.readUTF();
            get(request);
          }
          
          if (request.equals(Common.GET_LIST_CODE)) {
            getList();
          }
          
        } catch (IOException e) {
          throw new RuntimeException(e.getMessage()); // TODO need to handle exception when the socket falls.
        }
      }
    }
    
    private void put(String filename) throws IOException {
      File file = new File(Common.PATH_TO_SERVER_STORAGE + filename);
      if (file.exists()) file.delete();
      file.createNewFile();
      
      try (FileOutputStream fos = new FileOutputStream(file)) {
        byte[] buffer = new byte[8192];
        int edge;        
        while (in.available() > 0) {
          edge = in.read(buffer);
          fos.write(buffer, 0, edge);
        }
        System.out.println("File in the server storage, file length: " + file.length());
      }  
    }
    
    private void get(String filename) throws IOException {
      File file = new File(Common.PATH_TO_SERVER_STORAGE + filename);
      if (file.exists()) {
        out.writeUTF(Common.OK_STATUS);
        
        try (FileInputStream fis = new FileInputStream(file)) {
          byte[] buffer = new byte[8192];
          int edge;
          while (fis.available() > 0) {
            edge = fis.read(buffer);
            out.write(buffer, 0, edge);
          }
        }
        
      } else {
        out.writeUTF(Common.FAIL_STATUS);
      }
    }
    
    private void getList() throws IOException {
      File file = new File(Common.PATH_TO_SERVER_STORAGE);
      String[] list = file.list();
      StringBuilder formatString = new StringBuilder();
      for (int i = 0; i < list.length; i++) {
        formatString.append(list[i]);
        if (i != list.length - 1) {
          formatString.append("\n");
        }
      }
      out.writeUTF(formatString.toString());
    }
  }
  
  private ServerSocket server;
  private boolean isRunning = false;
  
  public Server() throws IOException {
    start();
  }
  
  public static void main(String[] args) {
    try {
      new Server();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void start() throws IOException {
    isRunning = true;
    server = new ServerSocket(Common.PORT);
    System.out.println("Server started...");
    
    while (isRunning) {
      Socket socket = server.accept();
      System.out.println("Client connected...");
      new Thread(new ClientRequestsHandler(socket)).start();
    }
  }
  
  public void stop() {
    isRunning = false;
  }
}
