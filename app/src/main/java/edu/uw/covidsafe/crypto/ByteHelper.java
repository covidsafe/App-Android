package edu.uw.covidsafe.crypto;

import java.util.List;

public class ByteHelper {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
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

    // concat byte arrays
    private static byte[] byteConcat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a,0,c,0,a.length);
        System.arraycopy(b,0,c,a.length, b.length);
        return c;
    }

    public static byte[] concat(List<byte[]> byteArray) {
        byte[] byteBuffer = new byte[0];
        for (byte[] b : byteArray) {
            byteBuffer = byteConcat(byteBuffer, b);
        }
        return byteBuffer;
    }
}
