package com.razielez.codec;

import java.util.Map;
import lombok.Data;

@Data
public class Message {

  private MessageType type;
  private Map<String, Object> mateData;
  private byte[] body;

  public static Message create(
      final MessageType type,
      final Map<String, Object> mateData,
      final byte[] body
  ) {
    Message message = new Message();
    message.setType(type);
    message.setMateData(mateData);
    message.setBody(body);
    return message;
  }

}
