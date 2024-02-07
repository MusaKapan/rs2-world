package com.einherji.rs2world.net.util;

import java.nio.ByteBuffer;

public final class Rs2WriteBuffer extends Rs2Buffer {

    /**
     * The position of the packet length in the packet header.
     */
    private int lengthPosition = 0;

    Rs2WriteBuffer(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    void switchAccessType(AccessType type) {
        switch (type) {
            case BIT_ACCESS:
                setBitPosition(getBuffer().position() * 8);
                break;
            case BYTE_ACCESS:
                getBuffer().position((getBitPosition() + 7) / 8);
                break;
        }
    }

    /**
     * Writes a packet header.
     *
     * @param cipher the encryptor
     * @param value  the value
     */
    public void writeHeader(ISAACCipher cipher, int value) {
        writeByte(value + cipher.getNextValue());
    }

    /**
     * Writes a packet header for a variable length packet. Note that the
     * corresponding "finishVariablePacketHeader" must be called to finish
     * the packet.
     *
     * @param cipher the ISAACCipher encryptor
     * @param value  the value
     */
    public void writeVariableHeader(ISAACCipher cipher, int value) {
        writeHeader(cipher, value);
        lengthPosition = getBuffer().position();
        writeByte(0);
    }

    /**
     * Writes a packet header for a variable length packet, where the length
     * is written as a short instead of a byte. Note that the corresponding
     * "finishVariableShortPacketHeader must be called to finish the packet.
     *
     * @param cipher the ISAACCipher encryptor
     * @param value  the value
     */
    public void writeVariableShortHeader(ISAACCipher cipher, int value) {
        writeHeader(cipher, value);
        lengthPosition = getBuffer().position();
        writeShort(0);
    }

    /**
     * Finishes a variable packet header by writing the actual packet length
     * at the length byte's position. Call this when the construction of the
     * actual variable length packet is complete.
     */
    public void finishVariableHeader() {
        getBuffer().put(lengthPosition, (byte) (getBuffer().position() - lengthPosition - 1));
    }

    /**
     * Finishes a variable packet header by writing the actual packet length
     * at the length short's position. Call this when the construction of
     * the variable length packet is complete.
     */
    public void finishVariableShortHeader() {
        getBuffer().putShort(lengthPosition, (short) (getBuffer().position() - lengthPosition - 2));
    }

    /**
     * Writes the bytes from the argued buffer into this buffer. This method does not modify the argued buffer,
     * and please do not flip() the buffer beforehand.
     */
    public void writeBytes(ByteBuffer src) {
        for (int i = 0; i < src.position(); i++) {
            writeByte(src.get(i));
        }
    }

    /**
     * Writes the bytes from the argued byte array into this buffer, in
     * reverse.
     *
     * @param data the data to write
     */
    public void writeBytesReverse(byte[] data) {
        for (int i = data.length - 1; i >= 0; i--) {
            writeByte(data[i]);
        }
    }

    /**
     * Writes the value as a variable amount of bits.
     *
     * @param amount the amount of bits
     * @param value  the value
     */
    public void writeBits(int amount, int value) {
        if (getAccessType() != AccessType.BIT_ACCESS) {
            throw new IllegalStateException("Illegal access type.");
        }
        if (amount < 0 || amount > 32) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 32 inclusive.");
        }

        int bytePos = getBitPosition() >> 3;
        int bitOffset = 8 - (getBitPosition() & 7);
        setBitPosition(getBitPosition() + amount);

        // Re-size the buffer if need be.
        int requiredSpace = bytePos - getBuffer().position() + 1;
        requiredSpace += (amount + 7) / 8;
        if (getBuffer().remaining() < requiredSpace) {
            ByteBuffer old = getBuffer();
            setBuffer(ByteBuffer.allocate(old.capacity() + requiredSpace));
            old.flip();
            getBuffer().put(old);
        }

        for (; amount > bitOffset; bitOffset = 8) {
            byte tmp = getBuffer().get(bytePos);
            tmp &= ~BIT_MASKS[bitOffset];
            tmp |= (value >> (amount - bitOffset)) & BIT_MASKS[bitOffset];
            getBuffer().put(bytePos++, tmp);
            amount -= bitOffset;
        }
        if (amount == bitOffset) {
            byte tmp = getBuffer().get(bytePos);
            tmp &= ~BIT_MASKS[bitOffset];
            tmp |= value & BIT_MASKS[bitOffset];
            getBuffer().put(bytePos, tmp);
        } else {
            byte tmp = getBuffer().get(bytePos);
            tmp &= ~(BIT_MASKS[amount] << (bitOffset - amount));
            tmp |= (value & BIT_MASKS[amount]) << (bitOffset - amount);
            getBuffer().put(bytePos, tmp);
        }
    }

    /**
     * Writes a boolean bit flag.
     *
     * @param flag the flag
     */
    public void writeBit(boolean flag) {
        writeBits(1, flag ? 1 : 0);
    }

    /**
     * Writes a value as a byte.
     *
     * @param value the value
     * @param type  the value type
     */
    public void writeByte(int value, ValueType type) {
        if (getAccessType() != AccessType.BYTE_ACCESS) {
            throw new IllegalStateException("Illegal access type.");
        }
        switch (type) {
            case A:
                value += 128;
                break;
            case C:
                value = -value;
                break;
            case S:
                value = 128 - value;
                break;
        }
        getBuffer().put((byte) value);
    }

    /**
     * Writes a value as a normal byte.
     *
     * @param value the value
     */
    public void writeByte(int value) {
        writeByte(value, ValueType.STANDARD);
    }

    /**
     * Writes a value as a short.
     *
     * @param value the value
     * @param type  the value type
     * @param order the byte order
     */
    public void writeShort(int value, ValueType type, ByteOrder order) {
        switch (order) {
            case BIG:
                writeByte(value >> 8);
                writeByte(value, type);
                break;
            case MIDDLE:
                throw new IllegalArgumentException("Middle-endian short is impossible!");
            case INVERSE_MIDDLE:
                throw new IllegalArgumentException("Inverse-middle-endian short is impossible!");
            case LITTLE:
                writeByte(value, type);
                writeByte(value >> 8);
                break;
        }
    }

    /**
     * Writes a value as a normal big-endian short.
     *
     * @param value the value.
     */
    public void writeShort(int value) {
        writeShort(value, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Writes a value as a big-endian short.
     *
     * @param value the value
     * @param type  the value type
     */
    public void writeShort(int value, ValueType type) {
        writeShort(value, type, ByteOrder.BIG);
    }

    /**
     * Writes a value as a standard short.
     *
     * @param value the value
     * @param order the byte order
     */
    public void writeShort(int value, ByteOrder order) {
        writeShort(value, ValueType.STANDARD, order);
    }

    /**
     * Writes a value as an int.
     *
     * @param value the value
     * @param type  the value type
     * @param order the byte order
     */
    public void writeInt(int value, ValueType type, ByteOrder order) {
        switch (order) {
            case BIG:
                writeByte(value >> 24);
                writeByte(value >> 16);
                writeByte(value >> 8);
                writeByte(value, type);
                break;
            case MIDDLE:
                writeByte(value >> 8);
                writeByte(value, type);
                writeByte(value >> 24);
                writeByte(value >> 16);
                break;
            case INVERSE_MIDDLE:
                writeByte(value >> 16);
                writeByte(value >> 24);
                writeByte(value, type);
                writeByte(value >> 8);
                break;
            case LITTLE:
                writeByte(value, type);
                writeByte(value >> 8);
                writeByte(value >> 16);
                writeByte(value >> 24);
                break;
        }
    }

    /**
     * Writes a value as a standard big-endian int.
     *
     * @param value the value
     */
    public void writeInt(int value) {
        writeInt(value, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Writes a value as a big-endian int.
     *
     * @param value the value
     * @param type  the value type
     */
    public void writeInt(int value, ValueType type) {
        writeInt(value, type, ByteOrder.BIG);
    }

    /**
     * Writes a value as a standard int.
     *
     * @param value the value
     * @param order the byte order
     */
    public void writeInt(int value, ByteOrder order) {
        writeInt(value, ValueType.STANDARD, order);
    }

    /**
     * Writes a value as a long.
     *
     * @param value the value
     * @param type  the value type
     * @param order the byte order
     */
    public void writeLong(long value, ValueType type, ByteOrder order) {
        switch (order) {
            case BIG:
                writeByte((int) (value >> 56));
                writeByte((int) (value >> 48));
                writeByte((int) (value >> 40));
                writeByte((int) (value >> 32));
                writeByte((int) (value >> 24));
                writeByte((int) (value >> 16));
                writeByte((int) (value >> 8));
                writeByte((int) value, type);
                break;
            case MIDDLE:
                throw new UnsupportedOperationException("Middle-endian long is not implemented!");
            case INVERSE_MIDDLE:
                throw new UnsupportedOperationException("Inverse-middle-endian long is not implemented!");
            case LITTLE:
                writeByte((int) value, type);
                writeByte((int) (value >> 8));
                writeByte((int) (value >> 16));
                writeByte((int) (value >> 24));
                writeByte((int) (value >> 32));
                writeByte((int) (value >> 40));
                writeByte((int) (value >> 48));
                writeByte((int) (value >> 56));
                break;
        }
    }

    /**
     * Writes a value as a standard big-endian long.
     *
     * @param value the value
     */
    public void writeLong(long value) {
        writeLong(value, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Writes a value as a big-endian long.
     *
     * @param value the value
     * @param type  the value type
     */
    public void writeLong(long value, ValueType type) {
        writeLong(value, type, ByteOrder.BIG);
    }

    /**
     * Writes a value as a standard long.
     *
     * @param value the value
     * @param order the byte order
     */
    public void writeLong(long value, ByteOrder order) {
        writeLong(value, ValueType.STANDARD, order);
    }

    /**
     * Writes a RuneScape string value (a null-terminated ASCII string).
     *
     * @param string the string
     */
    public void writeString(String string) {
        for (byte value : string.getBytes()) {
            writeByte(value);
        }
        writeByte(10);
    }

}