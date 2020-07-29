package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
  private class ClientRequestsHandler implements Runnable {
    private Socket socket;
    private DataInputStream in;
    
    public ClientRequestsHandler(Socket socket) throws IOException {
      System.out.println("The client in thread...");
      this.socket = socket;
      this.in = new DataInputStream(socket.getInputStream());
      System.out.println("i/o streams has been created...");
    }
    
    @Override
    public void run() {
      try {
        controller();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }    
    
    private void controller() throws IOException {
      String command = in.readUTF();
      System.out.println("command: " + command);
      
      if (command.equals(Common.PUT_FILE_CODE)) {
        put();
      }
      
      if (command.equals(Common.GET_FILE_CODE)) {
        get();
      }
      
      if (command.equals(Common.GET_LIST_CODE)) {
        getList();
      }
    }

    private void put() throws IOException {
      String filename = in.readUTF();
      
      File file = new File(Common.PATH_TO_SERVER_STORAGE + filename);
      if (file.exists()) file.delete();
      file.createNewFile();
      
      try (OutputStream fos = new FileOutputStream(file)) {
        byte[] buffer = new byte[8192];
        int edge;
        
        System.out.println("Bytes are ready to be recieved...");
        while ((edge = in.read(buffer)) != -1) {
          fos.write(buffer, 0, edge);
        }
        
        System.out.println("==Server== file length: " + file.length());
      }
      quite();
    }
    
    private void get() throws IOException {
      String filename = in.readUTF();
      File file = new File(Common.PATH_TO_SERVER_STORAGE + filename);
      try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
        if (file.exists()) {
          out.writeUTF(Common.OK_STATUS);
          
          InputStream fis = new FileInputStream(file);
          
          byte[] buffer = new byte[8192];
          int edge;
          
          System.out.println("Bytes are ready to be transmitted...");
          while (fis.available() > 0) {
            edge = fis.read(buffer);
            out.write(buffer, 0, edge);
          }
          
          fis.close();
          
          System.out.println("==Server== file length: " + file.length());
        } else {
          System.out.println("File " + filename + " does not exist in the server storage...");
          out.writeUTF(Common.OK_STATUS);
        }
      }
      quite();
    }
    
    private void getList() throws IOException {
      try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
        StringBuilder list = new StringBuilder();
        File storage = new File(Common.PATH_TO_SERVER_STORAGE);
        String[] files = storage.list();
        for (int i = 0; i < files.length; i++) {
          if (files[i].startsWith(".")) continue;
          list.append(files[i]).append("\n");
        }
        out.writeUTF(list.toString());
      }
      quite();
    }
    
    private void quite() throws IOException {
      in.close();
      socket.close();
    }
  }
  
  private ServerSocket server;
  private boolean running;
  
  public Server() throws IOException {
    start();
  }
  
  private void start() throws IOException {
    server = new ServerSocket(Common.PORT);
    System.out.println("Server has been started...");
    running = true;
    
    while (running) {
      Socket socket = server.accept();
      System.out.println("Client has been accepted...");
      
      new Thread(new ClientRequestsHandler(socket)).start();
    }
  }
  
  @SuppressWarnings("unused")
  private void stop() throws IOException {
    running = false;
    server.close();
    System.out.println("Server has been stopped...");
  }
  
  public static void main(String[] args) {
    try {
      new Server();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
