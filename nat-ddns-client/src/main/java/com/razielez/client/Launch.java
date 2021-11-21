package com.razielez.client;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launch {

  public static void main(String[] args) {
    final String serverHost = "127.0.0.1";
    final int serverPort = 9090;
    final int remotePort = 8080;
    final String proxyHost = "127.0.0.1";
    final int proxyPort = 1333;
    final String password = "asdasd";
    Launch launch = new Launch();
    launch.connect(serverHost, serverPort, password, remotePort, proxyHost, proxyPort);
  }

  private void connect(String serverHost, int serverPort, String password, int remotePort, String proxyHost, int proxyPort) {
    ClientChannelInitializer channelInitializer = new ClientChannelInitializer(remotePort, password, proxyHost, proxyPort);
    TcpConnection tcpConnection = new TcpConnection();
    try {
      ChannelFuture future = tcpConnection.connect(serverHost, serverPort, channelInitializer);
      future.addListener(x -> new Thread(new RetryConnect(serverHost, serverPort, password, remotePort, proxyHost, proxyPort)).start());
    } catch (InterruptedException e) {
      log.error("Connect error, ", e);
    }
  }

  class RetryConnect implements Runnable {

    private final String serverHost;
    private final int serverPort;
    private final String password;
    private final int remotePort;
    private final String proxyHost;
    private final int proxyPort;

    RetryConnect(String serverHost, int serverPort, String password, int remotePort, String proxyHost, int proxyPort) {
      this.serverHost = serverHost;
      this.serverPort = serverPort;
      this.password = password;
      this.remotePort = remotePort;
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
    }


    @Override
    public void run() {
      while (true) {
        try {
          connect(serverHost, serverPort, password, remotePort, proxyHost, proxyPort);
          break;
        } catch (Exception e) {
          log.error("Retry connect failed, ", e);
          try {
            Thread.sleep(10000);
          } catch (InterruptedException e1) {
            log.error("Error, ", e1);
          }
        }
      }
    }
  }


}
