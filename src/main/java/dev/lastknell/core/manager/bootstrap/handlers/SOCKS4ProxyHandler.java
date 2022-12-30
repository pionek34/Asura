package dev.lastknell.core.manager.bootstrap.handlers;

import dev.lastknell.core.manager.attack.handler.AttackChannelHandler;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.proxy.ProxyManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.proxy.Socks4ProxyHandler;
import org.jetbrains.annotations.NotNull;

public class SOCKS4ProxyHandler {
    private final ChannelInitializer<Channel> SOCKS4;

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

    public SOCKS4ProxyHandler(AttackChannelHandler handler, ProxyManager proxyManager, int timeout, boolean removeFailedProxy) {
        SOCKS4 = new ChannelInitializer<>() {
            @Override
            public void channelInactive(@NotNull ChannelHandlerContext ctx) {
                ctx.channel().close();
            }

            @Override
            protected void initChannel(final @NotNull Channel channel) {
                try {
                    final Proxy proxy = proxyManager.getProxy();
                    final Socks4ProxyHandler s;
                    if(proxy.requiresAuthentication()) {
                        s = new Socks4ProxyHandler(proxy.getAddress(), proxy.getEmail());
                    } else {
                        s = new Socks4ProxyHandler(proxy.getAddress());
                    }
                    s.setConnectTimeoutMillis(timeout);
                    s.connectFuture().addListener(f -> {
                        if(f.isSuccess() && s.isConnected()) {
                            handler.acceptChannel(channel, proxy);
                        } else {
                            if(removeFailedProxy) {
                                proxyManager.removeProxy(proxy);
                            }
                            channel.close();
                        }
                    });
                    channel.pipeline().addFirst(s).addLast(TAIL);
                } catch (Exception e) {
                    channel.close();
                }
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                ctx.close();
            }
        };
    }

    public ChannelInitializer<Channel> getSOCKS4() {
        return SOCKS4;
    }
}
