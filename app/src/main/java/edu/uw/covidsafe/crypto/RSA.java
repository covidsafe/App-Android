package edu.uw.covidsafe.crypto;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import edu.uw.covidsafe.utils.Constants;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class RSA {

    public static void generateKeyPair(Context cxt) throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 30);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, Constants.KEY_PROVIDER);
        KeyPairGeneratorSpec keyGenParameterSpec = new KeyPairGeneratorSpec.Builder(cxt)
                .setAlias(Constants.KEY_ALIAS)
                .setSubject(new X500Principal("CN=" + Constants.KEY_ALIAS))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .setKeySize(2048)
                .build();
        keyPairGenerator.initialize(keyGenParameterSpec);
        keyPairGenerator.generateKeyPair();
    }

    public static String encrypt(String plainstr) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, KeyStoreException, CertificateException, IOException, UnrecoverableEntryException, NoSuchProviderException {
        byte[] plaintext = plainstr.getBytes(Constants.CharSet);

        KeyStore keyStore = KeyStore.getInstance(Constants.KEY_PROVIDER);
        keyStore.load(null);

        KeyStore.PrivateKeyEntry privateKeyEntry =
                (KeyStore.PrivateKeyEntry) keyStore.getEntry(Constants.KEY_ALIAS, null);

        Cipher cipher;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
            cipher=Cipher.getInstance(Constants.RSA_SETTINGS, "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
        }
        else { // android m and above
            cipher=Cipher.getInstance(Constants.RSA_SETTINGS, "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }

        PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
        cipherOutputStream.write(plaintext);
        cipherOutputStream.close();

        return android.util.Base64.encodeToString(outputStream.toByteArray(),0);
    }

    public static String decrypt(String encryptedStr) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        byte[] encryptedData = android.util.Base64.decode(encryptedStr,0);

        KeyStore keyStore = KeyStore.getInstance(Constants.KEY_PROVIDER);
        keyStore.load(null);

        PrivateKey privateKey =
                (PrivateKey) keyStore.getKey(Constants.KEY_ALIAS, null);

        Cipher cipherDecrypt;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
            cipherDecrypt=Cipher.getInstance(Constants.RSA_SETTINGS, "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
        }
        else { // android m and above
            cipherDecrypt=Cipher.getInstance(Constants.RSA_SETTINGS, "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }

        cipherDecrypt.init(Cipher.DECRYPT_MODE, privateKey);

        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(encryptedData), cipherDecrypt);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte)nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i).byteValue();
        }

        return new String(bytes, Constants.CharSet);
    }

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