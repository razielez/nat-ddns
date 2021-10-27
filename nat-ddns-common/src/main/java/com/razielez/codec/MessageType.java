package com.razielez.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {

  AUTH(0x01, "认证"),
  CONNECT(0x02, "连接"),
  DISCONNECT(0x03, "断开连接"),
  TRANSFER(0x04, "数据传输"),
  HEARTBEAT(0x08, "心跳包");


  private final int code;
  private final String desc;


  public static MessageType valueOfCode(
      final byte code
  ) {
    for (MessageType value : MessageType.values()) {
      if (value.code == code) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown message type code: " + code);
  }

  public byte getByteCode() {
    return (byte) code;
  }

}
