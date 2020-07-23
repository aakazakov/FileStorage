package org.filestorage.common.prototype;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler {
  DataInputStream in;
  DataOutputStream out;
  Socket socket;
  ServerSocket server;

  public ClientHandler(ServerSocket server, Socket socket) {
    new Thread(() -> {
      try {
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.server = server;
        this.socket = socket;
        upload();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  public void upload() throws IOException {
    byte[] buffer = new byte[8192];
    int edge;
    String fileName = in.readUTF();
    File file = new File("./src/main/java/org/filestorage/common/prototype/storage/" + fileName);
    if (!file.exists()) {
      file.createNewFile();
      System.out.println("File " + file.getName() + " created.");
    } 
    FileOutputStream fos = new FileOutputStream(file, true);
    while ((edge = in.read(buffer)) != -1) {
      fos.write(buffer, 0, edge);
    }
    System.out.println("File " + file.getAbsolutePath() + " uploaded.");
  }
}
