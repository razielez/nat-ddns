package com.razielez.codec;

import java.util.HashMap;
import java.util.Map;

public record Message(
    Long serialNo,
    MessageType type,
    Map<String, Object> mateData,
    byte[] body
) {

  public static final Message HEARTBEAT = new Message(0L, MessageType.HEARTBEAT, null, null);

  public static Message create(
      final MessageType type,
      final Map<String, Object> mateData,
      final byte[] body
  ) {
    return new Message(0L, type, mateData, body);
  }

  public static Message create(
      final MessageType type,
      final Map<String, Object> mateData
  ) {
    return new Message(0L, type, mateData, null);
  }

  public static Message registerFailed(
      final String errorMsg
  ) {
    HashMap<String, Object> metaData = new HashMap<>();
    metaData.put("success", false);
    metaData.put("errorMsg", errorMsg);
    return new Message(0L, MessageType.AUTH, metaData, null);
  }
}
