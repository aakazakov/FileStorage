package org.filestorage.server.handlers;

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
    while (buf.isReadable()) {
      System.out.print((char) buf.readByte());
      System.out.flush();
    }
    buf.release();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }  
}
