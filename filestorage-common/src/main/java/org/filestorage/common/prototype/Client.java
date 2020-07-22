package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;

public class Client {
  public static void main(String[] args) {
    Client client = new Client();
 
    File file = new File("D:\\TEMP\\test.txt");
    client.put(file);
        
  }
  
  private void put(File file) {
    try (Socket socket = new Socket(Common.HOST, Common.PORT);
        InputStream in = new FileInputStream(file);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {
      
      byte[] buffer = new byte[8192];
      int edge;
      
      out.writeUTF(file.getName());
      
      while ((edge = in.read(buffer)) != -1) {
        System.out.println("client edge: " + edge);
        out.write(buffer, 0, edge);
      }         
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
