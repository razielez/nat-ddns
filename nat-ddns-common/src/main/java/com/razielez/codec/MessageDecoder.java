package com.razielez.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
    byte typeBytes = in.readByte();
    MessageType type = MessageType.valueOfCode(typeBytes);
    int metaLen = in.readInt();
    CharSequence metaSeq = in.readCharSequence(metaLen, StandardCharsets.UTF_8);
    Map<String, Object> metaData = new JSONObject(metaSeq).toMap();
    byte[] body = new byte[0];
    if (in.isReadable()) {
      body = ByteBufUtil.getBytes(in);
    }
    Message message = Message.create(type, metaData, body);
    out.add(message);
  }
}
