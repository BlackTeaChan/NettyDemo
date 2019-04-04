package com.blackteachan.netty.server;

import com.blackteachan.netty.view.ServerView;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Netty中，通讯的双方建立连接后，会把数据按照ByteBuf的方式进行传输，
 * 例如http协议中，就是通过HttpRequestDecoder对ByteBuf数据流进行处理，转换成http的对象。
 * @author blackteachan
 *
 */
public class TimeServer {

    //EventLoopGroup是用来处理IO操作的多线程事件循环器
    //bossGroup 用来接收进来的连接
    private EventLoopGroup bossGroup;
    //workerGroup 用来处理已经被接收的连接
    private EventLoopGroup workerGroup;

    private ChannelFuture f;

    public TimeServer() {
        ServerView.setCallback(new ServerView.OnCallback() {
            @Override
            public void onOpen(int port) {
                startServer(port);
            }
            @Override
            public void onClose() {
                closeServer();
            }
        });
    }

    public void startServer(final int port){
        new Thread(new Runnable() {
            public void run() {
                bossGroup = new NioEventLoopGroup();
                workerGroup = new NioEventLoopGroup();
                try {
                    //启动 NIO 服务的辅助启动类
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            //配置 Channel
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel ch) throws Exception {
                                    // 注册handler
                                    ch.pipeline().addLast(new ServerHandler());
                                }
                            })
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            .handler(new LoggingHandler(LogLevel.INFO))//配置日志输出
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    // 绑定端口，开始接收进来的连接
                    f = b.bind(port);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeServer(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 等待服务器 socket 关闭 。
                    f.channel().closeFuture();
                } finally {
                    // 优雅退出，释放线程池资源
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                    System.out.println("服务器优雅的释放了线程资源...");
                }
            }
        }).start();
    }
      
    public static void main(String[] args) throws Exception {
        new ServerView();
    }

}