package com.razielez.utils;

import com.google.gson.Gson;

final public class JsonTools {

  private static final Gson gson = new Gson();

  public static <T> T fromJson(String s, Class<T> clz) {
    return gson.fromJson(s, clz);
  }

  public static <T> String toJson(T t) {
    return gson.toJson(t);
  }

}

