package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;

public class EncryptionRequest extends DefinedPacket {

	public String serverId;
	public byte[] publicKey;
	public byte[] verifyToken;

	public void read(ByteBuf buf) {
		this.serverId = readString(buf);
		this.publicKey = readArray(buf);
		this.verifyToken = readArray(buf);
	}
}
