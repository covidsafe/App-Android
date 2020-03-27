package com.example.covidsafe.crypto;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

    private static final MessageDigest DIGEST;
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    // hexArray is needed for conversion of byte[] to hex
    // For more information, Look here: https://stackoverflow.com/a/9855338/7255359
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    static {
        try {
            DIGEST = MessageDigest.getInstance(Constant.ONE_WAY_HASH);  // SHA-256
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }

    public static byte[] hash(String data) throws NoSuchAlgorithmException {
        DIGEST.update(data.getBytes());
        byte[] bytes = DIGEST.digest();
        return bytes;
    }

    public static void hash(byte[] input, byte[] output) throws DigestException {
        DIGEST.update(input);
        DIGEST.digest(output, 0, 32);
    }

    // Convert byte[] to Hex encoding
    public static String convertBytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

    // Convert Hex to byte[]{32}
    public static byte[] hexStringToByteArray(String hexString){
        byte[] bytes = new byte[hexString.length() / 2];

        for(int i = 0; i < hexString.length(); i += 2){
            String sub = hexString.substring(i, i + 2);
            Integer intVal = Integer.parseInt(sub, 16);
            bytes[i / 2] = intVal.byteValue();
            String hex = "".format("0x%x", bytes[i / 2]);
        }
        return bytes;
    }

    public static synchronized byte[] hash(byte[] data) {
        return DIGEST.digest(data);
    }

    public static synchronized byte[] hash(byte[] data, int offset, int length) {
        DIGEST.update(data, offset, length);
        return DIGEST.digest();
    }
}
