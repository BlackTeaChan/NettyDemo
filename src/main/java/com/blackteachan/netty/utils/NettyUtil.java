package com.blackteachan.netty.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author blackteachan
 * @since 2021-03-26
 */
public class NettyUtil {

    /**
     * 发送文本到客户端
     * @param ctx 通道
     * @param text 文本
     */
    public static void send(ChannelHandlerContext ctx, String text){
        //在当前场景下，发送的数据必须转换成ByteBuf数组
        ByteBuf encoded = ctx.alloc().buffer(4 * text.length());
        encoded.writeBytes(text.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }

}
