package com.razielez.handler;

import com.razielez.HeartbeatHandler;
import com.razielez.client.TcpConnection;
import com.razielez.codec.Message;
import com.razielez.codec.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends HeartbeatHandler {

  private final int remotePort;
  private final String password;
  private final String proxyAddress;
  private final int proxyPort;

  private final Map<String, HeartbeatHandler> channelMap = new ConcurrentHashMap<>();
  private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


  public ClientHandler(int port, String password, String proxyAddress, int proxyPort) {
    this.remotePort = port;
    this.password = password;
    this.proxyAddress = proxyAddress;
    this.proxyPort = proxyPort;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    // register
    Map<String, Object> metaData = new HashMap<>();
    metaData.put("password", password);
    metaData.put("port", remotePort);
    Message message = Message.create(MessageType.AUTH, metaData, null);
    ctx.writeAndFlush(message);
    super.channelActive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Message message = (Message) msg;
    MessageType type = message.type();
    switch (type) {
      case AUTH:
        processAuth(message);
        break;
      case CONNECT:
        processConnect(message);
        break;
      case DISCONNECT:
        processDisConnect(message);
        break;
      case TRANSFER:
        processTransfer(message);
        break;
      case HEARTBEAT:
        processHeart(message);
        break;
    }
  }

  private void processHeart(Message message) {
    log.info("Heart data...");
  }

  private void processConnect(Message message) throws InterruptedException {
    ClientHandler clientHandler = this;
    TcpConnection localConnection = new TcpConnection();
    final String channelId = message.mateData().get("channelId").toString();
    try {
      localConnection.connect(proxyAddress, proxyPort, new ChannelInitializer() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
          LocalProxyHandler localProxyHandler = new LocalProxyHandler(clientHandler, channelId);
          ch.pipeline()
              .addLast(new ByteArrayDecoder())
              .addLast(new ByteArrayEncoder())
              .addLast(localProxyHandler);
          channelMap.put(channelId, localProxyHandler);
          channels.add(ch);
        }
      });
    } catch (InterruptedException e) {
      Map<String, Object> metaData = new HashMap<>();
      metaData.put("channelId", channelId);
      Message disConnectMsg = Message.create(MessageType.DISCONNECT, metaData, null);
      ctx.writeAndFlush(disConnectMsg);
      channelMap.remove(channelId);
      throw e;
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    channels.close();
    log.info("Loss connect server connect, restart!");
  }

  private void processTransfer(Message message) {
    final String channelId = message.mateData().get("channelId").toString();
    HeartbeatHandler handler = channelMap.get(channelId);
    if (handler != null) {
      ChannelHandlerContext ctx = handler.getCtx();
      ctx.writeAndFlush(message.body());
    }
  }

  private void processDisConnect(Message message) {
    final String channelId = message.mateData().get("channelId").toString();
    HeartbeatHandler handler = channelMap.get(channelId);
    if (handler != null) {
      handler.getCtx().close();
      channelMap.remove(channelId);
    }
  }

  private void processAuth(Message message) {
    Map<String, Object> mateData = message.mateData();
    Boolean success = (Boolean) mateData.get("success");
    if (Boolean.TRUE.equals(success)) {
      log.info("Register success");
    } else {
      log.info("Register failed:{} ", message);
      ctx.close();
    }

  }
}
