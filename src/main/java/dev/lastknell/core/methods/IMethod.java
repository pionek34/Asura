package dev.lastknell.core.methods;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.proxy.Proxy;
import io.netty.channel.Channel;

public interface IMethod {

    void accept(Channel channel, Proxy proxy);

    void init(NettyBootstrap service);

    String getName();

    /**
     * @return get Method description / know what method dose
     */
    String getDesc();

    boolean isExperimental();
}
