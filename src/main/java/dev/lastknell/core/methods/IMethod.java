package dev.lastknell.core.methods;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.proxy.Proxy;
import io.netty.channel.Channel;

public interface IMethod {

    public void accept(Channel channel, Proxy proxy);

    public void setService(NettyBootstrap service);

    public String getName();

    /**
     * @return get Method description / know what method dose
     */
    public String getDesc();

    public boolean isExperimental();
}
