package dev.lastknell.core.manager.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

public class NettyBootstrap {
    public final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyBootstrap(boolean usingEpoll, int workerThreads) {
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("NettyThread");
            thread.setPriority(usingEpoll ? 7 : 10);
            return thread;
        };

        Class<? extends Channel> socketChannel = usingEpoll ? EpollSocketChannel.class : NioSocketChannel.class;

        eventLoopGroup = usingEpoll ?
                         new EpollEventLoopGroup(workerThreads, threadFactory)
                                    :
                         new NioEventLoopGroup(workerThreads, threadFactory);
        bootstrap = new Bootstrap()
                .channel(socketChannel)
                .group(eventLoopGroup)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.AUTO_READ, Boolean.TRUE);
    }

    public void stop() {
        eventLoopGroup.shutdownGracefully();
    }

}
