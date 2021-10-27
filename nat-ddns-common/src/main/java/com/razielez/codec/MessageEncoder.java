package com.razielez.codec;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public final class MessageEncoder extends MessageToByteEncoder<Message> {

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf out) throws Exception {
    ByteOutputStream byteOutputStream = new ByteOutputStream();
    try (DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream)) {
      byte type = message.getType().getByteCode();
      dataOutputStream.writeByte(type);
      JSONObject metaData = new JSONObject(message.getMateData());
      byte[] metaBytes = metaData.toString().getBytes(StandardCharsets.UTF_8);
      byteOutputStream.write(metaBytes.length);
      byteOutputStream.write(metaBytes);
      byte[] body = message.getBody();
      byteOutputStream.write(body.length);
      byteOutputStream.write(body);
      byte[] data = byteOutputStream.getBytes();
      out.writeInt(data.length);
      out.writeBytes(data);
    }

  }
}
