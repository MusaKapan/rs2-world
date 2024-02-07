package com.einherji.rs2world.net.util;

import java.nio.ByteBuffer;

public final class Rs2ReadBuffer extends Rs2Buffer {

    Rs2ReadBuffer(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    void switchAccessType(AccessType type) {
        if (type == AccessType.BIT_ACCESS) {
            throw new UnsupportedOperationException("Reading bits is not implemented!");
        }
    }

    /**
     * Reads a value as a byte.
     *
     * @param signed the signedness
     * @param type   the value type
     * @return the value
     */
    public int readByte(boolean signed, ValueType type) {
        int value = getBuffer().get();
        switch (type) {
            case A:
                value = value - 128;
                break;
            case C:
                value = -value;
                break;
            case S:
                value = 128 - value;
                break;
        }
        return signed ? value : value & 0xff;
    }

    /**
     * Reads a standard signed byte.
     *
     * @return the value
     */
    public int readByte() {
        return readByte(true, ValueType.STANDARD);
    }

    /**
     * Reads a standard byte.
     *
     * @param signed the signedness
     * @return the value
     */
    public int readByte(boolean signed) {
        return readByte(signed, ValueType.STANDARD);
    }

    /**
     * Reads a signed byte.
     *
     * @param type the value type
     * @return the value
     */
    public int readByte(ValueType type) {
        return readByte(true, type);
    }

    /**
     * Reads a short value.
     *
     * @param signed the signedness
     * @param type   the value type
     * @param order  the byte order
     * @return the value
     */
    public int readShort(boolean signed, ValueType type, ByteOrder order) {
        int value = 0;
        switch (order) {
            case BIG:
                value |= readByte(false) << 8;
                value |= readByte(false, type);
                break;
            case MIDDLE:
                throw new UnsupportedOperationException("Middle-endian short is impossible!");
            case INVERSE_MIDDLE:
                throw new UnsupportedOperationException("Inverse-middle-endian short is impossible!");
            case LITTLE:
                value |= readByte(false, type);
                value |= readByte(false) << 8;
                break;
        }
        return signed ? value : value & 0xffff;
    }

    /**
     * Reads a standard signed big-endian short.
     *
     * @return the value
     */
    public int readShort() {
        return readShort(true, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a standard big-endian short.
     *
     * @param signed the signedness
     * @return the value
     */
    public int readShort(boolean signed) {
        return readShort(signed, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a signed big-endian short.
     *
     * @param type the value type
     * @return the value
     */
    public int readShort(ValueType type) {
        return readShort(true, type, ByteOrder.BIG);
    }

    /**
     * Reads a big-endian short.
     *
     * @param signed the signedness
     * @param type   the value type
     * @return the value
     */
    public int readShort(boolean signed, ValueType type) {
        return readShort(signed, type, ByteOrder.BIG);
    }

    /**
     * Reads a signed standard short.
     *
     * @param order the byte order
     * @return the value
     */
    public int readShort(ByteOrder order) {
        return readShort(true, ValueType.STANDARD, order);
    }

    /**
     * Reads a standard short.
     *
     * @param signed the signedness
     * @param order  the byte order
     * @return the value
     */
    public int readShort(boolean signed, ByteOrder order) {
        return readShort(signed, ValueType.STANDARD, order);
    }

    /**
     * Reads a signed short.
     *
     * @param type  the value type
     * @param order the byte order
     * @return the value
     */
    public int readShort(ValueType type, ByteOrder order) {
        return readShort(true, type, order);
    }

    /**
     * Reads an integer.
     *
     * @param signed the signedness
     * @param type   the value type
     * @param order  the byte order
     * @return the value
     */
    public long readInt(boolean signed, ValueType type, ByteOrder order) {
        long value = 0;
        switch (order) {
            case BIG:
                value |= readByte(false) << 24;
                value |= readByte(false) << 16;
                value |= readByte(false) << 8;
                value |= readByte(false, type);
                break;
            case MIDDLE:
                value |= readByte(false) << 8;
                value |= readByte(false, type);
                value |= readByte(false) << 24;
                value |= readByte(false) << 16;
                break;
            case INVERSE_MIDDLE:
                value |= readByte(false) << 16;
                value |= readByte(false) << 24;
                value |= readByte(false, type);
                value |= readByte(false) << 8;
                break;
            case LITTLE:
                value |= readByte(false, type);
                value |= readByte(false) << 8;
                value |= readByte(false) << 16;
                value |= readByte(false) << 24;
                break;
        }
        return signed ? value : value & 0xffffffffL;
    }

    /**
     * Reads a signed standard big-endian integer.
     *
     * @return the value
     */
    public int readInt() {
        return (int) readInt(true, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a standard big-endian integer.
     *
     * @param signed the signedness
     * @return the value
     */
    public long readInt(boolean signed) {
        return readInt(signed, ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a signed big-endian integer.
     *
     * @param type the value type
     * @return the value
     */
    public int readInt(ValueType type) {
        return (int) readInt(true, type, ByteOrder.BIG);
    }

    /**
     * Reads a big-endian integer.
     *
     * @param signed the signedness
     * @param type   the value type
     * @return the value
     */
    public long readInt(boolean signed, ValueType type) {
        return readInt(signed, type, ByteOrder.BIG);
    }

    /**
     * Reads a signed standard integer.
     *
     * @param order the byte order
     * @return the value
     */
    public int readInt(ByteOrder order) {
        return (int) readInt(true, ValueType.STANDARD, order);
    }

    /**
     * Reads a standard integer.
     *
     * @param signed the signedness
     * @param order  the byte order
     * @return the value
     */
    public long readInt(boolean signed, ByteOrder order) {
        return readInt(signed, ValueType.STANDARD, order);
    }

    /**
     * Reads a signed integer.
     *
     * @param type  the value type
     * @param order the byte order
     * @return the value
     */
    public int readInt(ValueType type, ByteOrder order) {
        return (int) readInt(true, type, order);
    }

    /**
     * Reads a signed long value.
     *
     * @param type  the value type
     * @param order the byte order
     * @return the value
     */
    public long readLong(ValueType type, ByteOrder order) {
        long value = 0;
        switch (order) {
            case BIG:
                value |= (long) readByte(false) << 56L;
                value |= (long) readByte(false) << 48L;
                value |= (long) readByte(false) << 40L;
                value |= (long) readByte(false) << 32L;
                value |= (long) readByte(false) << 24L;
                value |= (long) readByte(false) << 16L;
                value |= (long) readByte(false) << 8L;
                value |= readByte(false, type);
                break;
            case MIDDLE:
                throw new UnsupportedOperationException("middle-endian long is not implemented!");
            case INVERSE_MIDDLE:
                throw new UnsupportedOperationException("inverse-middle-endian long is not implemented!");
            case LITTLE:
                value |= readByte(false, type);
                value |= (long) readByte(false) << 8L;
                value |= (long) readByte(false) << 16L;
                value |= (long) readByte(false) << 24L;
                value |= (long) readByte(false) << 32L;
                value |= (long) readByte(false) << 40L;
                value |= (long) readByte(false) << 48L;
                value |= (long) readByte(false) << 56L;
                break;
        }
        return value;
    }

    /**
     * Reads a signed standard big-endian long.
     *
     * @return the value
     */
    public long readLong() {
        return readLong(ValueType.STANDARD, ByteOrder.BIG);
    }

    /**
     * Reads a signed big-endian long
     *
     * @param type the value type
     * @return the value
     */
    public long readLong(ValueType type) {
        return readLong(type, ByteOrder.BIG);
    }

    /**
     * Reads a signed standard long.
     *
     * @param order the byte order
     * @return the value
     */
    public long readLong(ByteOrder order) {
        return readLong(ValueType.STANDARD, order);
    }

    /**
     * Reads a RuneScape string value.
     *
     * @return the string
     */
    public String readString() {
        byte temp;
        StringBuilder b = new StringBuilder();
        while ((temp = (byte) readByte()) != 10) {
            b.append((char) temp);
        }
        return b.toString();
    }

    /**
     * Reads the amuont of bytes into the array, starting at the current
     * position.
     *
     * @param amount the amount to read
     * @return a buffer filled with the data
     */
    public byte[] readBytes(int amount) {
        return readBytes(amount, ValueType.STANDARD);
    }

    /**
     * Reads the amount of bytes into a byte array, starting at the current
     * position.
     *
     * @param amount the amount of bytes
     * @param type   the value type of each byte
     * @return a buffer filled with the data
     */
    public byte[] readBytes(int amount, ValueType type) {
        byte[] data = new byte[amount];
        for (int i = 0; i < amount; i++) {
            data[i] = (byte) readByte(type);
        }
        return data;
    }

    /**
     * Reads the amount of bytes from the buffer in reverse, starting at
     * current position + amount and reading in reverse until the current
     * position.
     *
     * @param amount the amount of bytes
     * @param type   the value type of each byte
     * @return a buffer filled with the data
     */
    public byte[] readBytesReverse(int amount, ValueType type) {
        byte[] data = new byte[amount];
        int dataPosition = 0;
        for (int i = getBuffer().position() + amount - 1; i >= getBuffer().position(); i--) {
            int value = getBuffer().get(i);
            switch (type) {
                case A:
                    value -= 128;
                    break;
                case C:
                    value = -value;
                    break;
                case S:
                    value = 128 - value;
                    break;
            }
            data[dataPosition++] = (byte) value;
        }
        return data;
    }

}