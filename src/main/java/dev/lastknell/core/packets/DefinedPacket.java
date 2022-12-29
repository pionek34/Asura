package dev.lastknell.core.packets;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DefinedPacket {

    public static Charset UTF_8 = Charset.forName("UTF-8");

    public static void writeString(String s, ByteBuf buf) {
        if (s.length() > 32767)
            throw new RuntimeException(
                    String.format("Cannot send string longer than Short.MAX_VALUE (got %s characters)", s.length()));
        byte[] b = s.getBytes(UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public static void writeStringNoCap(String s, ByteBuf buf) {
        byte[] b = s.getBytes(Charset.forName("UTF-16"));
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public String readString(ByteBuf buf) {
        int len = readVarInt(buf);
        if (len > 32767)
            throw new RuntimeException(
                    String.format("Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        byte[] b = new byte[len];
        buf.readBytes(b);
        return new String(b, UTF_8);
    }

    public static void writeString(String s, int maxLength, ByteBuf buf) {
        if (s.length() > maxLength)
            throw new RuntimeException(String.format("Cannot send string longer than %s (got %s characters)",
                    maxLength, s.length()));
        byte[] b = s.getBytes(UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public String readString(ByteBuf buf, int maxLength) {
        int len = readVarInt(buf);
        if (len > maxLength)
            throw new RuntimeException(String.format("Cannot receive string longer than %s (got %s characters)",
                    maxLength, len));
        byte[] b = new byte[len];
        buf.readBytes(b);
        return new String(b, UTF_8);
    }

    public static void writeArray(byte[] b, ByteBuf buf) {
        if (b.length > 32767)
            throw new RuntimeException(
                    String.format("Cannot send byte array longer than Short.MAX_VALUE (got %s bytes)",
                            b.length));
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public byte[] toArray(ByteBuf buf) {
        byte[] ret = new byte[buf.readableBytes()];
        buf.readBytes(ret);
        return ret;
    }

    public byte[] readArray(ByteBuf buf) {
        return readArray(buf, buf.readableBytes());
    }

    public byte[] readArray(ByteBuf buf, int limit) {
        int len = readVarInt(buf);
        if (len > limit)
            throw new RuntimeException(String.format("Cannot receive byte array longer than %s (got %s bytes)",
                    limit, len));
        byte[] ret = new byte[len];
        buf.readBytes(ret);
        return ret;
    }

    public int[] readVarIntArray(ByteBuf buf) {
        int len = readVarInt(buf);
        int[] ret = new int[len];
        for (int i = 0; i < len; i++)
            ret[i] = readVarInt(buf);
        return ret;
    }

    public static void writeStringArray(List<String> s, ByteBuf buf) {
        writeVarInt(s.size(), buf);
        for (String str : s)
            writeString(str, buf);
    }

    public List<String> readStringArray(ByteBuf buf) {
        int len = readVarInt(buf);
        List<String> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++)
            ret.add(readString(buf));
        return ret;
    }

    public int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        while (input.readableBytes() != 0 && maxBytes > 0) {
            byte in = input.readByte();
            out |= (in & Byte.MAX_VALUE) << bytes++ * 7;
            if (bytes > maxBytes)
                throw new RuntimeException("OVERSIZE_VAR_INT_EXCEPTION");
            if ((in & 0x80) != 128)
                return out;
        }
        throw new RuntimeException("No more bytes");
    }

    public static void writeVarInt(int value, ByteBuf output) {
        int part;
        while (value != 0) {
            part = value & 0x7F;
            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }
            output.writeByte(part);
        }
    }

    public int readVarShort(ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low &= 0x7FFF;
            high = buf.readUnsignedByte();
        }
        return (high & 0xFF) << 15 | low;
    }

    public static void writeVarShort(ByteBuf buf, int toWrite) {
        buf.writeShort(toWrite & 0x7FFF);
        if ((toWrite & 0x7F8000) != 0) {
            buf.writeByte((toWrite & 0x7F8000) >> 15);
        }
    }

    public static void writeUUID(UUID value, ByteBuf output) {
        output.writeLong(value.getMostSignificantBits());
        output.writeLong(value.getLeastSignificantBits());
    }

    public UUID readUUID(ByteBuf input) {
        return new UUID(input.readLong(), input.readLong());
    }

    public static void read(ByteBuf buf) {
        throw new UnsupportedOperationException("Packet must implement read method");
    }

    public static void write(ByteBuf buf) {
        throw new UnsupportedOperationException("Packet must implement write method");
    }
}
