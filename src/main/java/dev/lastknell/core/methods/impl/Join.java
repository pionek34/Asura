package dev.lastknell.core.methods.impl;

import dev.lastknell.core.NettyBootstrap;
import dev.lastknell.core.methods.IMethod;
import dev.lastknell.core.packets.Handshake;
import dev.lastknell.core.packets.LoginRequest;
import dev.lastknell.core.packets.NewLoginRequest;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.core.utils.RandomUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Join implements IMethod {

    NettyBootstrap service;
    private byte[] handshake;
    private boolean usenew = false;

    @Override
    public void accept(Channel channel, Proxy proxy) {
        service.oppnedCPS++;
        channel.writeAndFlush(Unpooled.buffer().writeBytes(handshake));
        if (usenew) {
            channel.writeAndFlush(Unpooled.buffer().writeBytes(
                    NewLoginRequest.getWrappedPacket(RandomUtils.randomString(12))));
        } else {
            channel.writeAndFlush(Unpooled.buffer().writeBytes(
                    LoginRequest.getWrappedPacket(RandomUtils.randomString(12))));
        }
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
        handshake = Handshake.getWrappedPacket(service.protocolID, service.srvIp, service.port, 2);
        if (service.protocolID > 758) {
            usenew = true;
        }
    }

}
