package com.razielez.codec;

import com.razielez.serialize.ProtostuffSerializer;
import com.razielez.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MessageEncoder extends MessageToByteEncoder<Message> {

  private static final Serializer serializer = ProtostuffSerializer.INSTANCE;

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf out) throws Exception {
    try {
      byte[] data = serializer.serialize(message);
      out.writeInt(data.length);
      out.writeBytes(data);
    } catch (Exception e) {
      log.error("Encode error, message:{}", message, e);
    }

  }
}
