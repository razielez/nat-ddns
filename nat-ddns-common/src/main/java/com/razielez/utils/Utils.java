package com.razielez.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Utils {

  public static boolean isEmpty(Object[] arr) {
    return arr == null || arr.length == 0;
  }


  public static String getRemoteIp() throws IOException {
    String url = "http://ipecho.net/plain";
    String command = String.format("wget %s -O - -q ; echo", url);
    return execCommand(command);
  }

  public static String execCommand(String command) throws IOException {
    InputStream is = Runtime.getRuntime().exec(command).getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    StringBuilder sb = new StringBuilder();
    String s;
    while ((s = br.readLine()) != null) {
      sb.append(s).append("\n");
    }
    return sb.toString();
  }
}
