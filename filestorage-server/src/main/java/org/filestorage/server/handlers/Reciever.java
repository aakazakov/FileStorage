package org.filestorage.server.handlers;

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
        ctx.writeAndFlush(new byte[] {action});
    }
  }
  
  private void put(ChannelHandlerContext ctx, ByteBuf buf) {
    System.out.println("Reading start...");
    System.out.println("Buffer info: " + buf);
    long start = System.currentTimeMillis(); // test.
    
    System.out.println("action: " + action);

    while (buf.isReadable()) {
      System.out.print((char) buf.readByte());
      System.out.flush();
    }
    
    System.out.println("\nBuffer info: " + buf);
    
    buf.release();
    
    ctx.writeAndFlush(new byte[] {Constants.PUT});
    
    System.out.println("\nReading finish... " + (System.currentTimeMillis() - start) + " ms.");
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
