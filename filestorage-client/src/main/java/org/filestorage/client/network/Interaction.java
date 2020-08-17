package org.filestorage.client.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.filestorage.common.Config;
import org.filestorage.common.Constants;

public class Interaction {

  private Socket socket;
  
  public void put() throws IOException {
    connect();
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
      out.write(Constants.PUT);
    }
  }
  
  private void connect() throws IOException {
    socket = new Socket(Config.HOST, Config.PORT);
  }
  
}
