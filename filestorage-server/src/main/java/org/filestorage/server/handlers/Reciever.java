package org.filestorage.server.handlers;

import org.filestorage.common.Constants;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class Reciever extends ChannelInboundHandlerAdapter  {

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
    ByteBuf buf = (ByteBuf) msg;
    byte command;
    System.out.println("Bytes in buffer: " + buf.writerIndex());
    System.out.println("Start reading...");
    while (buf.isReadable()) {
      command = buf.getByte(0);
      if (command == Constants.PUT) {
        System.out.print(command);
        System.out.flush();
      }
    }
    System.out.println("\nStop reading...");
    buf.release();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }  
}
