package com.blackteachan.netty.client;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger
            .getLogger(TimeClientHandler.class.getName());

    //535A584C3034
    private byte[] tempType = new byte[]{(byte)0x53, (byte)0x5A, (byte)0x58, (byte)0x4C, (byte)0x30, (byte)0x36};
    //020306004800CF01F92466
//	private byte[] temp = new byte[]{(byte)0x06, (byte)0x03, (byte)0x06, (byte)0x00, (byte)0x48, (byte)0x00, (byte)0xFF
//			, (byte)0x01, (byte)0xF9, (byte)0x24, (byte)0x66};
    public TimeClientHandler() {
//        byte[] req = "QUERY TIME ORDER".getBytes();
//        firstMessage = Unpooled.buffer(req.length);
//        firstMessage.writeBytes(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //与服务端建立连接后
        System.out.println("客户端active");
        ctx.writeAndFlush(Unpooled.copiedBuffer(tempType));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        System.out.println("客户端收到服务器响应数据");
        System.out.println(msg.toString());
        //服务端返回消息后
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        System.out.println("收到请求的长度是:" + req.length);
//
//        String strReq = bytesToHexString(req);
//        System.out.println("收到的请求是:" + strReq);
//        // 释放资源
//        logger.info("接收服务器响应msg:["+msg+"]");
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


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        System.out.println("客户端收到服务器响应数据处理完成");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.out.println("客户端异常退出");
        // 释放资源
        logger.warning("Unexpected exception from downstream:"
                + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}