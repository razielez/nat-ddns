package com.razielez;

public class Server {

  public void start(int port, String password) throws InterruptedException {
    TcpServer server = new TcpServer();
    server.bind(port, new ServerChannelInitializer(password));
  }

}
