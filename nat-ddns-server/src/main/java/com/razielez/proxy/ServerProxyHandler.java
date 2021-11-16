package com.razielez.proxy;

import com.razielez.HeartbeatHandler;
import com.razielez.TcpServer;
import com.razielez.codec.Message;
import com.razielez.codec.MessageType;
import com.razielez.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.StringUtil;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerProxyHandler extends HeartbeatHandler {

  private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  private final String password;
  private final TcpServer tcpServer = new TcpServer();
  private int port;

  private final boolean registered = false;

  public ServerProxyHandler(String password) {
    this.password = password;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    tcpServer.close();
    if (registered) {
      log.info("Sever stop on port: {}" , port);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Message message = (Message) msg;
    MessageType type = message.getType();
    if (MessageType.CONNECT.equals(type)) {
      processConnect(message);
    }
    if (registered) {
      switch (type) {
        case DISCONNECT:
          processDisConnect(message);
          break;
        case TRANSFER:
          processTransfer(message);
          break;
        case HEARTBEAT:
          processIDLE(message);
          break;
      }
    } else {
      ctx.close();
    }
  }

  private void processConnect(Message message) {
    HashMap<Object, Object> metaData = new HashMap<>();
    String password = message.getMateData().get("password").toString();
    boolean success = StringUtils.equals(password, this.password);
    Message resultMessage = null;
    if (success) {
      int port = (int) metaData.get("port");



    } else {
      resultMessage = Message.registerFailed("bad password");
    }

    ctx.writeAndFlush(resultMessage);

    if (!registered) {
      log.error("Register error, {}", metaData.get("reason"));
      ctx.close();
    }



  }

  private void processDisConnect(Message message) {
    Object channelId = message.getMateData().get("channelId");
    channels.close(x -> x.id().asLongText().equals(channelId));
  }

  private void processTransfer(Message message) {
    byte[] data = message.getBody();
    Object channelId = message.getMateData().get("channelId");
    channels.writeAndFlush(data, x -> x.id().asLongText().equals(channelId));
  }

  private void processIDLE(Message message) {

  }
}
