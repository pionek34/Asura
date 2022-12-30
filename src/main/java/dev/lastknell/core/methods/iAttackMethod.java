package dev.lastknell.core.methods;

import dev.lastknell.core.proxy.Proxy;
import io.netty.channel.Channel;

public interface iAttackMethod {
    void accept(Channel channel, Proxy proxy);

    String getName();

    String getDescription();
}
