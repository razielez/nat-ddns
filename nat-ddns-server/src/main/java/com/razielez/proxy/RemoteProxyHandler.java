package com.razielez.proxy;

import com.razielez.HeartbeatHandler;
import com.razielez.codec.Message;
import com.razielez.codec.MessageType;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;

public class RemoteProxyHandler extends HeartbeatHandler {

  private final HeartbeatHandler proxyHandler;


  public RemoteProxyHandler(HeartbeatHandler proxyHandler) {
    this.proxyHandler = proxyHandler;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    HashMap<String, Object> metaData = new HashMap<>();
    metaData.put("channelId", ctx.channel().id().asLongText());
    Message message = Message.create(MessageType.CONNECT, metaData);
    proxyHandler.getCtx().writeAndFlush(message);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    HashMap<String, Object> metaData = new HashMap<>();
    metaData.put("channelId", ctx.channel().id().asLongText());
    Message message = Message.create(MessageType.DISCONNECT, metaData);
    proxyHandler.getCtx().writeAndFlush(message);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    byte[] data = (byte[]) msg;
    HashMap<String, Object> metaData = new HashMap<>();
    metaData.put("channelId", ctx.channel().id().asLongText());
    Message message = Message.create(MessageType.TRANSFER, metaData, data);
    proxyHandler.getCtx().writeAndFlush(message);
  }
}
