package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class EncryptionResponse extends DefinedPacket {

    private final byte[] sharedSecret;
    private final byte[] verifyToken;

    public EncryptionResponse(byte[] sharedSecret, byte[] verifyToken) {
        this.sharedSecret = sharedSecret;
        this.verifyToken = verifyToken;
    }

    public void write(ByteBuf buf) {
        writeArray(this.sharedSecret, buf);
        writeArray(this.verifyToken, buf);
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
