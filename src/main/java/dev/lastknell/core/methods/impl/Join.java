package dev.lastknell.core.methods.impl;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.packets.Handshake;
import dev.lastknell.core.packets.LoginRequest;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.utils.RandomUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Join implements IMethod {

    NettyBootstrap service;

    private Handshake handshake;
  
    @Override
    public void accept(Channel channel, Proxy proxy) {
        service.oppnedCPS++;
        ByteBuf handshakBuf = Unpooled.buffer(0).writeBytes(handshake.getWrappedPacket());
        channel.writeAndFlush(handshakBuf);
        channel.writeAndFlush(Unpooled.buffer().writeBytes((new LoginRequest(RandomUtils.randomString(8)).getWrappedPacket())).release());
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
    public void init(NettyBootstrap service) {
        this.service = service;
        handshake = new Handshake(service.protocolID, service.srvIp, service.port, 2);
    }
    
}
