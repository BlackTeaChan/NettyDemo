package com.blackteachan.netty.client;

import com.blackteachan.netty.view.ClientView;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {

    private static final String HOST = "192.168.0.3";
    private static final int PORT = 18902;

    public void connect(int port, String host) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new StringDecoder());//设置字符串解码器
                            ch.pipeline().addLast(new TimeClientHandler());//设置客户端网络IO处理器
                        }
                    });
            //异步链接服务器 同步等待链接成功
            ChannelFuture f = b.connect(host, port).sync();
            //等待链接关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
            System.out.println("客户端优雅的释放了线程资源...");
        }
    }

    public static void main(String[] args) throws Exception {

        ClientView clientView = new ClientView();
        clientView.initView();
        new TimeClient().connect(PORT, HOST);
    }
}