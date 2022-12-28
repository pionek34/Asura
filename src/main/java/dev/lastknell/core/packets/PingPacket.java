package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PingPacket extends DefinedPacket {
	public static byte[] getWrappedPacket(long time) {
		ByteBuf allocated = Unpooled.buffer();
		allocated.writeByte(1).writeLong(time);
		ByteBuf wrapped = Unpooled.buffer();
		writeVarInt(allocated.readableBytes(), wrapped);
		wrapped.writeBytes(allocated);
		return wrapped.readBytes(wrapped.readableBytes()).array();
	}
}
