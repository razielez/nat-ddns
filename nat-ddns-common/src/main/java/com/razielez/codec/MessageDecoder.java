package com.razielez.codec;

import com.razielez.serialize.ProtostuffSerializer;
import com.razielez.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

  private static final Serializer serializer = ProtostuffSerializer.INSTANCE;

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
    if (in.readableBytes() < 4) {
      return;
    }
    try {
      in.markReaderIndex();
      int len = in.readInt();
      if (in.readableBytes() < len) {
        in.resetReaderIndex();
        return;
      }
      byte[] data = new byte[len];
      in.readBytes(data);
      out.add(serializer.deserialize(data, Message.class));
    } catch (Exception e) {
      log.error("Decode message error, in:{}", in, e);
    }
  }
}
