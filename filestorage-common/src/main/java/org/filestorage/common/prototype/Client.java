package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;

public class Client {
  Socket socket;
  DataOutputStream out;
  ClientHandler clientHandler;
  
  public Client() throws IOException {
    socket = new Socket(Common.HOST, Common.PORT);
    out = new DataOutputStream(socket.getOutputStream());
  }
  
  public void initHandler(ClientHandler clientHandler) {
    this.clientHandler = clientHandler;
  }
  
  public static void main(String[] args) {
    Client client;
    try {
      client = new Client();
      
      File file = new File("D:\\TEMP\\Robert Nystrom - Game Programming Patterns.pdf");
      client.put(file);
      
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
