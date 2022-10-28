package dev.lastknell.core.methods.impl;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.packets.Handshake;
import dev.lastknell.core.packets.LoginRequest;
import dev.lastknell.core.proxy.Proxy;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Join implements IMethod {

    NettyBootstrap service;

    private Handshake handshake;
  
    private volatile int i = 0;

    public Join() {
        handshake = new Handshake(service.protocolID, service.srvIp, service.port, 2);
    }

    @Override
    public void accept(Channel channel, Proxy proxy) {
        service.oppnedCPS++;
        channel.writeAndFlush(Unpooled.buffer().writeBytes(handshake.getWrappedPacket()));
        channel.writeAndFlush(Unpooled.buffer().writeBytes((new LoginRequest(String.valueOf(this.i++))).getWrappedPacket()));
        service.successfulCPS++;
    }

    @Override
    public String getName() {
        return "Join";
    }

    @Override
    public String getDesc() {
        return "connect random named bots to server";
    }

    @Override
    public boolean isExperimental() {
        return false;
    }

    @Override
    public void setService(NettyBootstrap service) {
        this.service = service;
    }
    
}
