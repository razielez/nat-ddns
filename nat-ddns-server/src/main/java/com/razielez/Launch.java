package com.razielez;

public class Launch {


  public static void main(String[] args) {
    final String password = "asdasd";
    final Integer port = 8080;
    try {
      new Server().start(port, password);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
