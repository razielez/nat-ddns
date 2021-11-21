package com.razielez;

public class ServerLaunch {


  public static void main(String[] args) {
    final String password = "asdasd";
    final int port = 8085;
    try {
      new Server().start(port, password);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
