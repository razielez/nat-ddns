package com.razielez.codec;

import java.util.Map;
import lombok.Data;

@Data
public class Message {

  private MessageType type;
  private Map<String, Object> mateData;
  private byte[] body;

}
