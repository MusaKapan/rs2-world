package com.einherji.rs2world.net.util;

import java.nio.ByteBuffer;

/**
 * An abstract parent class for two buffer type objects, one for reading data
 * and one for writing data. Provides static factory methods for initializing
 * these child buffers and some basic data manipulation.
 *
 * @author blakeman8192
 */
public abstract class Rs2Buffer {

    /**
     * An enum whose values represent the possible order in which bytes are
     * written in a multiple-byte value. Also known as "endianness".
     *
     * @author blakeman8192
     */
    public enum ByteOrder {
        LITTLE, BIG, MIDDLE, INVERSE_MIDDLE
    }

    /**
     * An enum whose values represent the possible custom RuneScape value types.
     * Type A is to add 128 to the value, type C is to invert the value, and
     * type S is to subtract the value from 128. Of course, STANDARD is just the
     * normal data value.
     *
     * @author blakeman8192
     */
    public enum ValueType {
        STANDARD, A, C, S
    }

    /**
     * An enum whose values represent the current type of access to a
     * StreamBuffer. BYTE_ACCESS is for reading/writing bytes, BIT_ACCESS is for
     * reading/writing bits.
     *
     * @author blakeman8192
     */
    public enum AccessType {
        BYTE_ACCESS, BIT_ACCESS
    }

    public static final int[] BIT_MASKS = {
            0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff, 0x7fff, 0xffff,
            0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
            0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1
    };

    private AccessType accessType = AccessType.BYTE_ACCESS;
    private int bitPosition = 0;
    private ByteBuffer buffer;

    protected Rs2Buffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static Rs2ReadBuffer createReadBuffer(ByteBuffer data) {
        return new Rs2ReadBuffer(data);
    }

    public static Rs2WriteBuffer createWriteBuffer(ByteBuffer buffer) {
        return new Rs2WriteBuffer(buffer);
    }

    /**
     * Handles the internal switching of the access type.
     *
     * @param type the new access type
     */
    abstract void switchAccessType(AccessType type);

    /**
     * Gets the AccessType of this StreamBuffer.
     *
     * @return the current AccessType
     */
    public AccessType getAccessType() {
        return accessType;
    }

    /**
     * Sets the AccessType of this StreamBuffer.
     *
     * @param accessType the new AccessType
     */
    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
        switchAccessType(accessType);
    }

    /**
     * Gets the current bit position.
     *
     * @return the bit position
     */
    public int getBitPosition() {
        return bitPosition;
    }

    /**
     * Sets the bit position.
     *
     * @param bitPosition the new bit position
     */
    public void setBitPosition(int bitPosition) {
        this.bitPosition = bitPosition;
    }


    public ByteBuffer getBuffer() {
        return buffer;
    }

    void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
}