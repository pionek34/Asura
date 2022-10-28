package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class LoginRequest extends DefinedPacket {

    public String data;

    public LoginRequest(String data) {
        this.data = data;
    }

    public void write(ByteBuf buf) {
        writeString(this.data, buf);
    }

    public void writeNoCap(ByteBuf buf) {
        writeStringNoCap(this.data, buf);
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