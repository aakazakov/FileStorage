package org.filestorage.common.prototype;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  Socket socket;

  public Client() throws IOException {
    connect();
    put(new File("d:/tmp/test.pdf"));
  }

  private void connect() throws IOException {
    socket = new Socket(Common.HOST, Common.PORT);
  }
  
  @SuppressWarnings("unused")
  private void disconnect() throws IOException {
    socket.close();
  }

  private void put(File file) throws IOException {
    if (file.exists()) {

      try (DataInputStream in = new DataInputStream(socket.getInputStream());
          DataOutputStream out = new DataOutputStream(socket.getOutputStream());
          InputStream fis = new FileInputStream(file);) {
        System.out.println("i/o streams has been created...");

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

  public static void main(String[] args) {
    try {
      new Client();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}