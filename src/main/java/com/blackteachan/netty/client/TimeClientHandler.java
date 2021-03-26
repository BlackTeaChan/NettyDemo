package com.blackteachan.netty.client;

import com.blackteachan.netty.map.ChannelMap;
import com.blackteachan.netty.utils.NettyUtil;
import com.blackteachan.netty.view.ClientView;
import com.blackteachan.netty.view.ServerView;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    @Setter
    private static Callback callback = null;

    TimeClientHandler(String host, int port){
//        ClientView.setServerHandlerCallback();
        ClientView.setSendCallback(new ClientView.SendCallback() {
            @Override
            public void onSend(String rIP, String text) {
                ChannelHandlerContext ctx = ChannelMap.get(host);
                if(ctx != null) {
                    NettyUtil.send(ctx, text);
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String rec = msg.toString();
        log.info("客户端收到: " + rec);

        try {
            callback.receive(rec);
        }catch (Exception e){
            log.error("客户端处理出错: " + e.getMessage());
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("客户端异常退出");
        cause.printStackTrace();
        ctx.close();
    }

    public interface Callback{
        void receive(String string);
    }
}