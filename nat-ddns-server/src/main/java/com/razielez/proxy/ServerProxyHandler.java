package com.razielez.proxy;

import com.razielez.HeartbeatHandler;
import com.razielez.TcpServer;
import com.razielez.codec.Message;
import com.razielez.codec.MessageType;
import com.razielez.utils.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerProxyHandler extends HeartbeatHandler {

  private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
  private final String password;
  private final TcpServer remoteServer = new TcpServer();
  private int port;

  private boolean registered = false;

  public ServerProxyHandler(String password) {
    this.password = password;
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    remoteServer.close();
    if (registered) {
      log.info("Sever stop on port: {}", port);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Message message = (Message) msg;
    MessageType type = message.getType();
    if (MessageType.AUTH.equals(type)) {
      processConnect(message);
    }
    if (!registered) {
      ctx.close();
    }
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
  }

  private void processConnect(Message message) {
    HashMap<String, Object> metaData = new HashMap<>();
    String password = message.getMateData().get("password").toString();
    boolean success = StringUtils.equals(password, this.password);
    if (success) {
      int port = (int) message.getMateData().get("port");
      ServerProxyHandler handler = this;
      try {
        remoteServer.bind(port, new ChannelInitializer() {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            ch.pipeline().addLast(
                new ByteArrayDecoder(),
                new ByteArrayEncoder(),
                new RemoteProxyHandler(handler)
            );
            channels.add(ch);
          }
        });
        metaData.put("success", true);
        this.port = port;
        registered = true;
        log.info("Register success, start server on port:{}", port);
      } catch (InterruptedException e) {
        log.error("Bind error, ", e);
        metaData.put("success", false);
        metaData.put("reason", e.getMessage());
      }
    } else {
      metaData.put("success", false);
      metaData.put("reason", "Pwd is wrong");
    }
    Message result = new Message();
    result.setType(MessageType.AUTH);
    result.setMateData(metaData);
    ctx.writeAndFlush(result);
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
