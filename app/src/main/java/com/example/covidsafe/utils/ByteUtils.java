package com.example.covidsafe.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ByteUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static byte[] uuid2bytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    public static String byte2string(byte[] b) {
        ByteBuffer bb = ByteBuffer.wrap(b);
        UUID id = new UUID(bb.getLong(),bb.getLong());
        return id.toString();
    }
}