package com.blackteachan.netty.server;

import com.blackteachan.netty.map.ChannelMap;
import com.blackteachan.netty.view.ServerView;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;


/**
 * 服务端处理
 * @author blackteachan
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger log = Logger.getLogger(ServerHandler.class);
    private static OnCallback mOnCallback;

    ServerHandler(){
        ServerView.setServerHandlerCallback();
        ServerView.setSendCallback(new ServerView.SendCallback() {
            @Override
            public void onSend(String rIP, String text) {
                ChannelHandlerContext ctx = ChannelMap.get(rIP);
                if(ctx != null) {
                    send(ctx, text);
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            System.out.println("SimpleServerHandler.channelRead");
            ByteBuf result = (ByteBuf) msg;
            byte[] result1 = new byte[result.readableBytes()];

            // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
            result.readBytes(result1);
            String resultStr = new String(result1);

            // 接收并打印客户端的信息
            System.out.println("Client said:" + resultStr);

            // 释放资源，这行很关键
            result.release();

            // 向客户端发送消息
            String response = "hello client!\r\n";
            send(ctx, response);
        }catch (Exception e){
            log.info("ServerHandler error: " + e);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ChannelMap.add(ctx, ctx.channel().remoteAddress().toString());
        mOnCallback.addChannel(ctx);
        log.info(ctx.channel().remoteAddress() + " - Active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ChannelMap.remove(ctx);
        mOnCallback.removeChannel(ctx);
        log.info(ctx.channel().remoteAddress() + " - Inactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 发送文本到客户端
     * @param ctx 通道
     * @param text 文本
     */
    public void send(ChannelHandlerContext ctx, String text){
        // 在当前场景下，发送的数据必须转换成ByteBuf数组
        ByteBuf encoded = ctx.alloc().buffer(4 * text.length());
        encoded.writeBytes(text.getBytes());
        ctx.write(encoded);
        ctx.flush();
    }


    /**
     * 设置回调
     * @param onCallback
     */
    public static void setOnCallback(OnCallback onCallback){
        mOnCallback = onCallback;
    }

    /**
     * 回调类
     */
    public static abstract class OnCallback{
        public abstract void addChannel(ChannelHandlerContext ctx);
        public abstract void removeChannel(ChannelHandlerContext ctx);
    }
  
}
