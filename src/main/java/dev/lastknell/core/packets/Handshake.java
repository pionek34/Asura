package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Handshake extends DefinedPacket {
    public static byte[] getWrappedPacket(int protocolVersion, String host, int port, int requestedProtocol) {
        ByteBuf allocated = Unpooled.buffer();
        allocated.writeByte(0);
        writeVarInt(protocolVersion, allocated);
        writeString(host, allocated);
        allocated.writeShort(port);
        writeVarInt(requestedProtocol, allocated);
        ByteBuf wrapped = Unpooled.buffer();
        writeVarInt(allocated.readableBytes(), wrapped);
        wrapped.writeBytes(allocated);
        byte[] bytes = new byte[wrapped.readableBytes()];
        wrapped.getBytes(0, bytes);
        wrapped.release();
        return bytes;
    }
}
