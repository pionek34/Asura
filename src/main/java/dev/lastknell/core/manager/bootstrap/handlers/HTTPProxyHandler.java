package dev.lastknell.core.manager.bootstrap.handlers;

import dev.lastknell.core.manager.attack.handler.AttackChannelHandler;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.proxy.ProxyManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.proxy.HttpProxyHandler;
import org.jetbrains.annotations.NotNull;

public class HTTPProxyHandler {
    private final ChannelInitializer<Channel> HTTP;

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

    public HTTPProxyHandler(AttackChannelHandler handler, ProxyManager proxyManager, int timeout, boolean removeFailedProxy) {
        HTTP = new ChannelInitializer<>() {
            @Override
            public void channelInactive(@NotNull ChannelHandlerContext ctx) {
                ctx.channel().close();
            }

            @Override
            protected void initChannel(final @NotNull Channel channel) {
                try {
                    final Proxy proxy = proxyManager.getProxy();
                    final HttpProxyHandler s;
                    if(proxy.requiresAuthentication()) {
                        s = new HttpProxyHandler(proxy.getAddress(), proxy.getEmail(), proxy.getPassword());
                    } else {
                        s = new HttpProxyHandler(proxy.getAddress());
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

    public ChannelInitializer<Channel> getHTTP() {
        return HTTP;
    }
}
