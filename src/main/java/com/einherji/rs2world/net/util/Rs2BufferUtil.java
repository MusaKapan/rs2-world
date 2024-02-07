package com.einherji.rs2world.net.util;

public class Rs2BufferUtil {

    private static final char[] TEXT_UNPACK_TABLE = {
            ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b',
            'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',',
            ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']'
    };

    private static final char[] DECODE_BUFFER = new char[4096];


    public static String textUnpack(byte packedData[], int size) {
        int idx = 0, msn = -1;

        for (int i = 0; i < size * 2; i++) {
            int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xF;

            if (msn == -1) {
                if (val < 13)
                    DECODE_BUFFER[idx++] = TEXT_UNPACK_TABLE[val];
                else
                    msn = val;
            } else {
                DECODE_BUFFER[idx++] = TEXT_UNPACK_TABLE[((msn << 4) + val) - 195];
                msn = -1;
            }
        }
        return new String(DECODE_BUFFER, 0, idx);
    }

    public static int hexToInt(byte[] data) {
        int value = 0;
        int n = 1000;

        for (byte b : data) {
            int num = (b & 0xFF) * n;
            value += num;

            if (n > 1) {
                n = n / 1000;
            }
        }
        return value;
    }
}
