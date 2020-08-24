package org.filestorage.server.handlers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.filestorage.common.Constants;
import org.filestorage.common.Utility;
import org.filestorage.common.entity.FileInfo;
import org.filestorage.common.entity.FileList;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MainHandler extends ChannelInboundHandlerAdapter {
  
  private byte action;
  private Path file;
  private long fileSize;
  private FileList fileList;
  
  public MainHandler() {
    action = 0;
    file = null;
    fileSize = 0;
    fileList = new FileList();
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
      case Constants.REMOVE:
        remove(ctx, buf);
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
        file = null;
        fileSize = 0;
        ctx.writeAndFlush(new byte[] { Constants.PUT });
      }
    }
  }
  
  private void createFile(ByteBuf buf) throws IOException {
    initFile(buf);
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
  
  // do not work yet ))
  private void get(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
    if (file == null) {
      initFile(buf);
      ctx.writeAndFlush(new byte[] { Constants.GET });
    } else if (fileSize == 0) {
      fileSize = Files.size(file);
      buf.release();
      ctx.writeAndFlush(Utility.longToBytes(fileSize));
    } else {
      buf.release();
      InputStream in = Files.newInputStream(file);
      byte[] b = new byte[512];
      while (in.available() > 0) {
        in.read(b);
        ctx.writeAndFlush(b);
      }
      in.close();
    }
  }
  
  private void initFile(ByteBuf buf) {
    StringBuilder fileName = new StringBuilder();
    while (buf.isReadable()) {
      fileName.append((char) buf.readByte());
    }
    buf.release();
    file = Paths.get("TMP_STORAGE/").resolve(fileName.toString());
  }
  
  private void getList(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
    List<FileInfo> list = Files.list(Paths.get("TMP_STORAGE"))
        .map(FileInfo::new)
        .collect(Collectors.toList());
    fileList.setList(list);
    
    ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
    ObjectOutputStream objOut = new ObjectOutputStream(byteArr);
    objOut.writeObject(fileList);
    objOut.close();
    ctx.writeAndFlush(byteArr.toByteArray());
  }
  
  private void remove(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
    StringBuilder filename = new StringBuilder();    
    while (buf.isReadable()) {
      filename.append((char) buf.readByte());
    }
    buf.release();
    
    Path path = Paths.get("TMP_STORAGE").resolve(filename.toString());    
    boolean removed = Files.deleteIfExists(path);   
    if (removed) {
      ctx.writeAndFlush(new byte[] { Constants.REMOVE });
    } else {
      ctx.writeAndFlush(new byte[] { Constants.FAIL });
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.writeAndFlush(new byte[] { Constants.FAIL });
    Files.deleteIfExists(file);
    cause.printStackTrace();
    ctx.close();
  }
}
