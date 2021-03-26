package com.blackteachan.netty.client;

import com.blackteachan.netty.constants.ClientConstants;
import com.blackteachan.netty.view.ClientView;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public class TimeClient {

//    private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static Channel channel;

    private static State state = State.STOPPED;
    @Setter
    private static StateCallback stateCallback = null;

    public static ChannelFuture start(String host, int port) throws Exception {
        ChannelFuture f = null;
        try {
            //客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            workerGroup = new NioEventLoopGroup();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ClientConstants.CONNECT_TIMEOUT_MILLIS)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new StringDecoder());//设置字符串解码器
                            ch.pipeline().addLast(new TimeClientHandler());//设置客户端网络IO处理器
                        }
                    });
            //异步链接服务器 同步等待链接成功
            f = b.connect(host, port).sync();
            channel = f.channel();
            state = State.RUNNING;
            stateCallback.connected(channel);
        } finally {
            if (f != null && f.isSuccess()) {
                state = State.RUNNING;
                log.info("Netty客户端已连接 - " + host + ":" + port);
            } else {
                state = State.STOPPED;
                log.error("Netty客户端启动失败");
            }
        }
        return f;
    }

    public static void shutdown(){
        if(state.equals(State.RUNNING)) {
            channel.close();
            workerGroup.shutdownGracefully();
            state = State.STOPPED;
            stateCallback.disconnected(channel);
        }
    }

    public interface StateCallback{
        void connected(Channel channel);
        void disconnected(Channel channel);
    }

    /**
     * 服务端状态
     */
    public enum State{
        /**
         * 运行
         */
        RUNNING,
        /**
         * 停止
         */
        STOPPED
    }
}