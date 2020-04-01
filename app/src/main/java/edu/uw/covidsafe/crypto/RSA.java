package edu.uw.covidsafe.crypto;

import android.os.Build;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class RSA {

    public static KeyPair generateKeyPair(int securityBits) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(securityBits, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature =
                Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(signature);
        } else {
            return org.apache.commons.codec.binary.Base64.encodeBase64String(signature);
        }
    }

    public static byte[] signAsBytes(String plainText, PrivateKey privateKey) throws Exception {
        java.security.Signature privateSignature =
                java.security.Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();
        return signature;
    }

    public static boolean verify(String plainText, String signature,
                                 PublicKey publicKey) throws Exception {
        java.security.Signature publicSignature =
                java.security.Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            signatureBytes = Base64.getDecoder().decode(signature);
        } else {
            signatureBytes = org.apache.commons.codec.binary.Base64.decodeBase64(signature);
        }

        return publicSignature.verify(signatureBytes);
    }

    public static boolean verifyAsBytes(String plainText, byte[] signatureBytes,
                                        PublicKey publicKey) throws Exception {
        java.security.Signature publicSignature =
                java.security.Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        return publicSignature.verify(signatureBytes);
    }

    public static byte[] encrypt(byte[] plainTextData, PublicKey publicKey) throws
            NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(Constant.RSA_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainTextData);
    }

    public static byte[] decrypt(byte[] encryptedData, PrivateKey privateKey) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(Constant.RSA_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }
}