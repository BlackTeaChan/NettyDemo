package com.blackteachan.netty.server;

import com.blackteachan.netty.map.ChannelMap;
import com.blackteachan.netty.utils.NettyUtil;
import com.blackteachan.netty.view.ServerView;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j;


/**
 * 服务端处理
 * @author blackteachan
 */
@Log4j
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private static OnCallback mOnCallback;

    TimeServerHandler(){
        ServerView.setServerHandlerCallback();
        ServerView.setSendCallback(new ServerView.SendCallback() {
            @Override
            public void onSend(String rIP, String text) {
                ChannelHandlerContext ctx = ChannelMap.get(rIP);
                if(ctx != null) {
                    NettyUtil.send(ctx, text);
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {

            String rec = msg.toString();
            log.info("服务端收到: " + rec);

        }catch (Exception e){
            log.error("服务端处理出错: " + e);
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
