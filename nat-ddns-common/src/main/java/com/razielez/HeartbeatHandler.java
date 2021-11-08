package com.razielez;

import com.razielez.codec.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

  @Getter
  protected ChannelHandlerContext ctx;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error("Channel exception caught", cause.getCause());
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleState state = ((IdleStateEvent) evt).state();
      switch (state) {
        case READER_IDLE:
          processReaderIdle();
          break;
        case WRITER_IDLE:
          processWriterIdle();
          break;
      }
    }
  }

  private void processReaderIdle() {
    log.info("Client close, remote: {}", ctx.channel().remoteAddress());
    ctx.close();
  }

  private void processWriterIdle() {
    ctx.writeAndFlush(Message.HEARTBEAT);
  }
}
