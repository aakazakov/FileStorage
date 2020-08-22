package org.filestorage.server.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.filestorage.common.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MainHandler extends ChannelInboundHandlerAdapter  {
  
  private byte action;
  private Path file;
  private long fileSize;
  
  public MainHandler() {
    action = 0;
    file = null;
    fileSize = 0;
  }
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Channel has been activated. Context name: " + ctx.name());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("\nChannel has been inactivated. Context name: " + ctx.name());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {   
    ByteBuf buf = (ByteBuf) msg;
    switch (action) {
      case Constants.PUT:
        put(ctx, buf);
        break;
      case Constants.GET:
        get(ctx, buf);
        break;
      case Constants.GET_LIST:
        getList(ctx, buf);
        break;
      default:
        action = buf.getByte(0);
        ctx.writeAndFlush(new byte[] { action });
    }
  }
  
  private void put(ChannelHandlerContext ctx, ByteBuf buf) throws IOException { 
    if (file == null) {
      createFile(buf);
      ctx.writeAndFlush(new byte[] { Constants.PUT });
    } else if (fileSize == 0) {
      initFileSize(buf);
      ctx.writeAndFlush(new byte[] { Constants.PUT });
    } else {
      writeFile(buf);
      if (Files.size(file) == fileSize) {
        ctx.writeAndFlush(new byte[] { Constants.PUT }); 
      }
    }
  }
  
  private void createFile(ByteBuf buf) throws IOException {
    StringBuilder fileName = new StringBuilder();
    while (buf.isReadable()) {
      fileName.append((char) buf.readByte());
    }
    buf.release();
    file = Paths.get("TMP_STORAGE/").resolve(fileName.toString());
    Files.deleteIfExists(file);
    Files.createFile(file);
  }
  
  private void initFileSize(ByteBuf buf) {
    fileSize = buf.readLong();
    buf.release();
  }
  
  private void writeFile(ByteBuf buf) throws IOException {
    byte[] bytes = new byte[buf.writerIndex()];
    buf.readBytes(bytes);
    Files.write(file, bytes, StandardOpenOption.APPEND);
    buf.release();
  }
  
  private void get(ChannelHandlerContext ctx, ByteBuf buf) {
    System.out.println("ctx: " + ctx + " / msg: " + buf);
  }
  
  private void getList(ChannelHandlerContext ctx, ByteBuf buf) {
    System.out.println("ctx: " + ctx + " / msg: " + buf);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }  
}
