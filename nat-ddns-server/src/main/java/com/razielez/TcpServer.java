package com.razielez;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpServer {

  private Channel ch;

  public synchronized void bind(
      final int port,
      final ChannelInitializer channelInitializer
  ) throws InterruptedException {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    NioEventLoopGroup workGroup = new NioEventLoopGroup();
    try {

      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(channelInitializer)
          .childOption(ChannelOption.SO_KEEPALIVE, true);

      ch = b.bind().sync().channel();
      ch.closeFuture().addListener(x -> {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
          }
      );
    } catch (Exception e) {
      workGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
      throw e;
    }
  }

  public void close() {
    if (ch != null) {
      ch.close();
    }
  }

}
