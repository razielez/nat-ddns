package com.razielez.codec;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

  public static final Message HEARTBEAT = new Message(MessageType.HEARTBEAT);

  /**
   * 消息流水号
   */
  private Long serialNo;
  private MessageType type;
  private Map<String, Object> mateData;
  private byte[] body;

  public Message(MessageType type) {
    this.type = type;
  }

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
