package com.razielez.handler;

import com.razielez.HeartbeatHandler;
import com.razielez.codec.Message;
import com.razielez.codec.MessageType;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;

public class LocalProxyHandler extends HeartbeatHandler {

  private final HeartbeatHandler proxyHandler;
  private final String remoteChannelId;


  public LocalProxyHandler(HeartbeatHandler proxyHandler, String remoteChannelId) {
    this.proxyHandler = proxyHandler;
    this.remoteChannelId = remoteChannelId;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    byte[] data = (byte[]) msg;
    Message message = new Message();
    message.setType(MessageType.TRANSFER);
    message.setBody(data);
    Map<String, Object> metaData = new HashMap<>();
    metaData.put("channelId", remoteChannelId);
    message.setMateData(metaData);
    proxyHandler.getCtx().writeAndFlush(message);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    // disConnect
    Map<String, Object> metaData = new HashMap<>();
    metaData.put("channelId", remoteChannelId);
    Message message = Message.create(MessageType.DISCONNECT, metaData, null);
    proxyHandler.getCtx().writeAndFlush(message);
  }
}
