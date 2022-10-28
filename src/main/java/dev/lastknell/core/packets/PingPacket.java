package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PingPacket extends DefinedPacket {
    public long time;

    public PingPacket(long time) {
		this.time = time;
	}

	public void write(ByteBuf buf) {
		buf.writeLong(this.time);
	}

    public byte[] getWrappedPacket() {
		ByteBuf allocated = Unpooled.buffer();
		allocated.writeByte(1);
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
