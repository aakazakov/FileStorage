package org.filestorage.server;

import org.filestorage.common.Config;
import org.filestorage.server.handlers.MainHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;

public class ServerApp {

  public void run() throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
       .channel(NioServerSocketChannel.class)
       .childHandler(new ChannelInitializer<SocketChannel>() {

         @Override
         protected void initChannel(SocketChannel ch) throws Exception {
           ch.pipeline().addLast(new ByteArrayEncoder(), new MainHandler());
         }
       });
      ChannelFuture future = b.bind(Config.PORT).sync();
      System.out.println("Server has been started...");
      future.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
  
  public static void main(String[] args) {
    new Thread (() -> {
      try {
        new ServerApp().run();
      } catch (Exception e) {
        e.printStackTrace();
      } 
     }).start();
  }
}
