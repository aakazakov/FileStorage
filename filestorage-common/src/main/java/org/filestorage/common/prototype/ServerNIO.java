package org.filestorage.common.prototype;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class ServerNIO implements Runnable {
  private ServerSocketChannel server;
  private ByteBuffer buffer;
  private Selector selector;

  public ServerNIO() throws IOException {
    buffer = ByteBuffer.allocate(2048);
    server = ServerSocketChannel.open();
    server.socket().bind(new InetSocketAddress(Common.PORT));
    server.configureBlocking(false);
    selector = Selector.open();
    server.register(selector, SelectionKey.OP_ACCEPT);
  }

  @Override
  public void run() {
    try {
      System.out.println("Server has been started...");

      while (server.isOpen()) {
        selector.select();

        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          iterator.remove();

          if (key.isAcceptable()) {
            System.out.println("Client has been accepted...");

            SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            System.out.println("Server is ready to read...");
          }

          if (key.isReadable()) {
            System.out.println("Start reading...");
            buffer.clear();
            int edge = ((SocketChannel) key.channel()).read(buffer);
            if (edge == -1) {
              key.channel().close();
            }

            buffer.flip();

            StringBuilder str = new StringBuilder();
            while (buffer.hasRemaining()) {
              str.append((char) buffer.get());
            }
            
            
            byte[] b = buffer.array();
            int limit = buffer.limit();
            
            System.out.println(str);
            System.out.println("End reading...");
            
            System.out.println(b[limit - 1]);
            if (b[limit - 1] == -1) {
              ((SocketChannel) key.channel()).write(ByteBuffer.wrap("Server catched -1".getBytes()));
              key.channel().close();
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    try {
      new Thread(new ServerNIO()).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
