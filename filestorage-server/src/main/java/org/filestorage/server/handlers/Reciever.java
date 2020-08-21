package org.filestorage.server.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.filestorage.common.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class Reciever extends ChannelInboundHandlerAdapter  {
  
  private byte action;
  
  public Reciever() {
    action = 0;
  }
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Channel has been activated. Context name: " + ctx.name());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Channel has been inactivated. Context name: " + ctx.name());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {   
    switch (action) {
      case Constants.PUT:
        put(ctx, msg);
        break;
      case Constants.GET:
        get(ctx, msg);
        break;
      case Constants.GET_LIST:
        getList(ctx, msg);
        break;
      default:
        ByteBuf buf = (ByteBuf) msg;
        action = buf.getByte(0);
        ctx.writeAndFlush(new byte[] {action});
    }
  }
  
  private void put(ChannelHandlerContext ctx, Object msg) throws IOException {  
    if (msg instanceof String) {
      String fileName = (String) msg;
      createFile(fileName);
      System.out.println(fileName);
      ctx.writeAndFlush(new byte[] {Constants.PUT});
    } else {
      ByteBuf buf = (ByteBuf) msg;
      
      System.out.println("Buffer info: " + buf);
      System.out.println("action: " + action);
      System.out.println("Reading start...");
      long start = System.currentTimeMillis();

      while (buf.isReadable()) {
        System.out.print((char) buf.readByte());
        System.out.flush();
      }
      
      System.out.println("Buffer info: " + buf);
      
      buf.release();     
      
      ctx.writeAndFlush(new byte[] {Constants.PUT});
      
      System.out.println("\nReading finish... " + (System.currentTimeMillis() - start) + " ms.");
    }
  }
  
  private boolean createFile(String fileName) throws IOException { // Can i be here ????
    Path path = (Paths.get("./TMP_STORAGE/" + fileName));
    System.out.println("./TMP_STORAGE/" + fileName);
    Files.deleteIfExists(path);
    Files.createFile(path);
    return Files.exists(path);
  }
  
  private void get(ChannelHandlerContext ctx, Object msg) {
    System.out.println("ctx: " + ctx + " / msg: " + msg);
  }
  
  private void getList(ChannelHandlerContext ctx, Object msg) {
    System.out.println("ctx: " + ctx + " / msg: " + msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }  
}
