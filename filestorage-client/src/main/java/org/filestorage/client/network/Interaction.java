package org.filestorage.client.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.filestorage.common.Config;
import org.filestorage.common.Constants;
import org.filestorage.common.Utility;
import org.filestorage.common.entity.FileList;
import org.filestorage.common.exceptions.OnProcessException;

public class Interaction {

  private Socket socket;
  
  public void put(File file) throws IOException, OnProcessException {
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
        throw new OnProcessException("send PUT command", serverResponse);
      out.write(file.getName().getBytes());
      serverResponse = in.readByte();
      if (serverResponse != Constants.PUT)
        throw new OnProcessException("send file name", serverResponse);
      out.write(Utility.longToBytes(file.length()));
      serverResponse = in.readByte();
      if (serverResponse != Constants.PUT)
        throw new OnProcessException("send file length", serverResponse);
      
      while (fis.available() > 0) {
        edge = fis.read(buffer);
        out.write(buffer, 0, edge);
      }
     
      serverResponse = in.readByte();
      if (serverResponse != Constants.PUT)
        throw new OnProcessException("just wait server OK", serverResponse);
    }
    disconnect();
  }
  
  public FileList getFileList() throws IOException, OnProcessException, ClassNotFoundException {
    FileList fileList = null;
    connect();   
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());) {
      byte serverResponse;
      
      out.writeByte(Constants.GET_LIST);          
      serverResponse = in.readByte();
      if (serverResponse != Constants.GET_LIST)
        throw new OnProcessException("send GET_LIST command", serverResponse);
      
      out.writeByte(Constants.GET_LIST);
      
      ObjectInputStream objIn = new ObjectInputStream(in);
      fileList = (FileList) objIn.readObject();
      objIn.close();
    }    
    disconnect();
    return fileList;
  }
  
  public void removeFile(String filename) throws IOException, OnProcessException {
    connect();   
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());) {
      byte serverResponse;
      
      out.writeByte(Constants.REMOVE);
      serverResponse = in.readByte();
      if (serverResponse != Constants.REMOVE)
        throw new OnProcessException("send REMOVE command", serverResponse);
           
      out.write(filename.getBytes());
      serverResponse = in.readByte();
      if (serverResponse != Constants.REMOVE)
        throw new OnProcessException("send the filename to remove", serverResponse);
    }   
    disconnect();
  }
  
  public void get(String filename) throws IOException, OnProcessException {
    connect();  
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        DataInputStream in = new DataInputStream(socket.getInputStream());) {
      byte serverResponse;
      byte[] buffer = new byte[512];
      int edge;
      long fileSize;
          
      out.writeByte(Constants.GET);
      serverResponse = in.readByte();
      if (serverResponse != Constants.GET)
        throw new OnProcessException("send GET command", serverResponse);
      
      out.write(filename.getBytes());
      serverResponse = in.readByte();
      if (serverResponse != Constants.GET)
        throw new OnProcessException("send the filename to download", serverResponse);
      
      out.writeByte(Constants.GET);
      fileSize = in.readLong();

      File file = createFile(filename);
      FileOutputStream fos = new FileOutputStream(file, true);
      out.writeByte(Constants.GET);
      while ((edge = in.read(buffer)) != -1) {
        for (int i = 0; i < edge; i++) {
          fos.write(buffer[i]);
        }
      }
      fos.close();
      
      if (file.length() != fileSize) {
        long currentSize = file.length();
        if (file.exists()) file.delete();
        throw new OnProcessException(
            "Couldn't download file. Current file size " + currentSize + " instead of " + fileSize);
      }
    }   
    disconnect();
  }
  
  private File createFile(String path) throws IOException {
    File file = new File("C:/Users/Public/" + path);
    if (file.exists()) file.delete();
    file.createNewFile();
    return file;
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
