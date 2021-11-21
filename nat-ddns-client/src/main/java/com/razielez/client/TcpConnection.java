package com.razielez.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpConnection {

  public ChannelFuture connect(
      final String host,
      final int port,
      final ChannelInitializer channelInitializer
  ) throws InterruptedException {
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    Bootstrap b = new Bootstrap();
    b.group(workerGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .handler(channelInitializer)
    ;
    try {
      Channel channel = b.connect(host, port).sync().channel();
      return channel.closeFuture().addListener(x -> workerGroup.shutdownGracefully());
    } catch (InterruptedException e) {
      log.error("Connect error, ", e);
      workerGroup.shutdownGracefully();
      throw e;
    }
  }

}
