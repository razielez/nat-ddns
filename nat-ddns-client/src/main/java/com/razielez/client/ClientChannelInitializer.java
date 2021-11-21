package com.razielez.client;

import com.razielez.codec.MessageDecoder;
import com.razielez.codec.MessageEncoder;
import com.razielez.handler.ClientHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientChannelInitializer extends ChannelInitializer {

  private final int remotePort;
  private final String password;
  private final String proxtHost;
  private final int proxyPort;

  public ClientChannelInitializer(int remotePort, String password, String proxtHost, int proxyPort) {
    this.remotePort = remotePort;
    this.password = password;
    this.proxtHost = proxtHost;
    this.proxyPort = proxyPort;
  }

  @Override
  protected void initChannel(Channel ch) throws Exception {
    ClientHandler clientHandler = new ClientHandler(remotePort, password, proxtHost, proxyPort);
    ch.pipeline()
        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
        .addLast(new MessageDecoder())
        .addLast(new MessageEncoder())
        .addLast(new IdleStateHandler(60, 30, 0))
        .addLast(clientHandler);
  }
}
