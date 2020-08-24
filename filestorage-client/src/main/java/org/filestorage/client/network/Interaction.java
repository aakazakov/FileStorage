package org.filestorage.client.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.filestorage.common.Config;
import org.filestorage.common.Constants;
import org.filestorage.common.Utility;
import org.filestorage.common.entity.FileList;
import org.filestorage.common.exceptions.OnServerException;

public class Interaction {

  private Socket socket;
  
  public void put(File file) throws IOException, OnServerException {
    connect();
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());
        FileInputStream fis = new FileInputStream(file);) {     
      byte[] buffer = new byte[512];
      int edge;
      byte serverResponse;
      
      // TODO code below needs optimization)).
      out.writeByte(Constants.PUT);
      serverResponse = in.readByte();
      if (serverResponse != Constants.PUT)
        throw new OnServerException("send PUT command", serverResponse);
      out.write(file.getName().getBytes());
      serverResponse = in.readByte();
      if (serverResponse != Constants.PUT)
        throw new OnServerException("send file name", serverResponse);
      out.write(Utility.longToBytes(file.length()));
      serverResponse = in.readByte();
      if (serverResponse != Constants.PUT)
        throw new OnServerException("send file length", serverResponse);
      
      while (fis.available() > 0) {
        edge = fis.read(buffer);
        out.write(buffer, 0, edge);
      }
     
      serverResponse = in.readByte();
      if (serverResponse != Constants.PUT)
        throw new OnServerException("just wait server OK", serverResponse);
    }
    disconnect();
  }
  
  public FileList getFileList() throws IOException, OnServerException, ClassNotFoundException {
    FileList fileList = null;
    connect();   
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());) {
      byte serverResponse;
      
      out.writeByte(Constants.GET_LIST);          
      serverResponse = in.readByte();
      if (serverResponse != Constants.GET_LIST)
        throw new OnServerException("send GET_LIST command", serverResponse);
      
      out.writeByte(Constants.GET_LIST);
      
      ObjectInputStream objIn = new ObjectInputStream(in);
      fileList = (FileList) objIn.readObject();
      objIn.close();
    }    
    disconnect();
    return fileList;
  }
    
  private void connect() throws IOException {
    socket = new Socket(Config.HOST, Config.PORT);
    System.out.println("Сlient connected to server...");
  }
  
  private void disconnect() throws IOException {
    socket.close();
    System.out.println("Connection terminated...");
  }  
}
