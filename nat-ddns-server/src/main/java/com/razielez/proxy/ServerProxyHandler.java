package com.razielez.proxy;

import com.razielez.HeartbeatHandler;
import com.razielez.TcpServer;
import com.razielez.codec.Message;
import com.razielez.codec.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

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

  }

  private void processDisConnect(Message message) {

  }

  private void processTransfer(Message message) {

  }

  private void processIDLE(Message message) {

  }
}
