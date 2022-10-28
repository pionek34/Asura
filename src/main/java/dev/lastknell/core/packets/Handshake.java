package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Handshake extends DefinedPacket {

    public int protocolVersion;
    public String host;
    public int port;
    public int requestedProtocol;

    public Handshake(int protocolVersion, String host, int port, int requestedProtocol) {
        this.protocolVersion = protocolVersion;
        this.host = host;
        this.port = port;
        this.requestedProtocol = requestedProtocol;
    }

    public void write(ByteBuf buf) {
        writeVarInt(this.protocolVersion, buf);
        writeString(this.host, buf);
        buf.writeShort(this.port);
        writeVarInt(this.requestedProtocol, buf);
    }

    public byte[] getWrappedPacket() {
        ByteBuf allocated = Unpooled.buffer();
        allocated.writeByte(0);
        write(allocated);
        ByteBuf wrapped = Unpooled.buffer();
        writeVarInt(allocated.readableBytes(), wrapped);
        wrapped.writeBytes(allocated);
        byte[] bytes = new byte[wrapped.readableBytes()];
        wrapped.getBytes(0, bytes);
        wrapped.release();
        return bytes;
    }
}
