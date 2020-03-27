package com.example.covidsafe.crypto;

public class Constant {
    public static final String ONE_WAY_HASH = "SHA-256";
    public static final String AES = "AES";  // Uses CBC by default.

    // Note this is actually not ECB. There is no ECB in RSA.
    public static final String RSA_CIPHER = "RSA/ECB/PKCS1Padding";
}
