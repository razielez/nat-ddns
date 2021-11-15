package com.razielez;

import com.razielez.codec.MessageDecoder;
import com.razielez.codec.MessageEncoder;
import com.razielez.proxy.ServerProxyHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

  private final String password;

  public ServerChannelInitializer(String password) {
    this.password = password;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ServerProxyHandler serverHandler = new ServerProxyHandler(password);
    ch.pipeline()
        .addLast(
            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
            new MessageDecoder(),
            new MessageEncoder(),
            new IdleStateHandler(60, 30, 0),
            serverHandler
        );
  }
}
