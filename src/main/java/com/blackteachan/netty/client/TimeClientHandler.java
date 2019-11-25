package com.blackteachan.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    @Setter
    private static Callback callback = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String rec = msg.toString();
        System.out.println("收到:" + rec);

        try {
            callback.receive(rec);
        }catch (Exception e){
            log.error("Socket客户端处理出错: " + e.getMessage());
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress().toString() + "激活");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress().toString() + "闲置");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        System.out.println("客户端收到服务器响应数据处理完成");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("客户端异常退出");
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 把字节数组转换成16进制字符串
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public interface Callback{
        void receive(String string);
    }
}