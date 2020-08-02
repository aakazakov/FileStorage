package org.filestorage.common.prototype;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Iterator;

public class ServerNIO {
  private class ClientRequestsHandler {
    private void accept(SelectionKey key) throws IOException {
      SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
      channel.configureBlocking(false);
      System.out.println("Client has been accepted...");
      channel.register(selector, SelectionKey.OP_READ);
      System.out.println("Server is ready to read...");
    }
    
    private void read(SelectionKey key) throws IOException {
      System.out.println("Start reading...");
      buffer.clear();
      SocketChannel channel = (SocketChannel) key.channel();
      int edge = channel.read(buffer);
      if (edge == -1) {
        channel.close();
      }

      buffer.flip();
      
      if (buffer.hasRemaining() && buffer.get() == Common.SIGNAL) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
          bytes[i] = buffer.get();
        }
        long length = Common.bytesToLong(bytes);
        System.out.println(length);
      } else {
        buffer.position(0);
      }
      
      StringBuilder str = new StringBuilder();
      while (buffer.hasRemaining()) {
        char ch = (char) buffer.get();
        str.append(ch);
      }

      System.out.println(str);

      System.out.println("End reading...");
    }
  }
  
  private ServerSocketChannel server;
  private ByteBuffer buffer;
  private Selector selector;
  private ClientRequestsHandler handler;

  public ServerNIO() throws IOException {
    handler = new ClientRequestsHandler();
    buffer = ByteBuffer.allocate(2048);
    server = ServerSocketChannel.open();
    server.socket().bind(new InetSocketAddress(Common.PORT));
    server.configureBlocking(false);
    selector = Selector.open();
    server.register(selector, SelectionKey.OP_ACCEPT);
  }

  public void run() throws IOException {
    System.out.println("Server has been started...");

    while (server.isOpen()) {
      selector.select();

      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        iterator.remove();

        if (key.isAcceptable()) {
          handler.accept(key);
        }

        if (key.isReadable()) {
          handler.read(key);
        }
      }
    }
  }

  public static void main(String[] args) {
    try {
      new ServerNIO().run();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
