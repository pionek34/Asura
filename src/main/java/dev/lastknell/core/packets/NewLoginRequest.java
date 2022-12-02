package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NewLoginRequest extends DefinedPacket {

    public static byte[] getWrappedPacket(String name) {
        ByteBuf allocated = Unpooled.buffer();
        allocated.writeByte(0);
        writeString(name, allocated);
        allocated.writeBoolean(false);
        allocated.writeBoolean(false);
        ByteBuf wrapped = Unpooled.buffer();
        writeVarInt(allocated.readableBytes(), wrapped);
        wrapped.writeBytes(allocated);
        byte[] bytes = new byte[wrapped.readableBytes()];
        wrapped.getBytes(0, bytes);
        wrapped.release();
        return bytes;
    }

}
