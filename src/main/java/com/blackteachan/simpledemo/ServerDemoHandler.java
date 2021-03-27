package com.blackteachan.simpledemo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author blackteachan
 * @since 2021-03-27 20:00
 */
public class ServerDemoHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(msg.toString());
    }
}
