package edu.uw.covidsafe.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;

import com.example.covidsafe.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import edu.uw.covidsafe.utils.Constants;

public class AES256 {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void generateKeyStoreKeys() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, Constants.KEY_PROVIDER);
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(Constants.KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build();
        keyGenerator.init(keyGenParameterSpec);
        keyGenerator.generateKey();
    }

    public static String encryptWithKeyStore(byte[] plainTextMessage) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // convert string to byte[] for encryption
        long t1 = System.currentTimeMillis();
//        byte[] plainTextMessage = plaintext.getBytes(Constants.CharSet);

        // get key store, generate cipher IV
        // prepend IV to all data we encrypt and store
        if (Constants.keyStore == null) {
            Constants.keyStore = KeyStore.getInstance(Constants.KEY_PROVIDER);
            Constants.keyStore.load(null);
        }

        if (Constants.secretKey == null) {
            KeyStore.SecretKeyEntry secretKeyEntry =
                    (KeyStore.SecretKeyEntry)Constants.keyStore.getEntry(Constants.KEY_ALIAS, null);
            Constants.secretKey = secretKeyEntry.getSecretKey();
        }

        Cipher cipher = Cipher.getInstance(Constants.AES_SETTINGS);

        // prepend IV to all data we encrypt and store
        cipher.init(Cipher.ENCRYPT_MODE, Constants.secretKey);

//        Log.e("aes","init "+(System.currentTimeMillis()-t1)+"");
        t1 = System.currentTimeMillis();
        byte[] encryptedBytes = cipher.doFinal(plainTextMessage);
        byte[] ivBytes = cipher.getIV();

//        Log.e("aes","do final "+(System.currentTimeMillis()-t1)+"");
        t1 = System.currentTimeMillis();

        byte[] encryptedWhole = new byte[ivBytes.length+encryptedBytes.length];
        int counter = 0;
        for (int i = 0; i < ivBytes.length; i++) {
            encryptedWhole[counter++] = ivBytes[i];
        }

        for (int i = 0; i < encryptedBytes.length; i++) {
            encryptedWhole[counter++] = encryptedBytes[i];
        }

        // return base64 encoded string
        String ss = android.util.Base64.encodeToString(encryptedWhole,0);

        return ss;
    }

    public static byte[] decryptWithKeyStore(String encryptedStr) throws NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableEntryException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        // decode base64 string
        byte[] dataToDecryptWhole = android.util.Base64.decode(encryptedStr,0);

        // get secret key from key store
        if (Constants.keyStore == null) {
            Constants.keyStore = KeyStore.getInstance(Constants.KEY_PROVIDER);
            Constants.keyStore.load(null);
        }

        if (Constants.secretKey == null) {
            KeyStore.SecretKeyEntry secretKeyEntry =
                    (KeyStore.SecretKeyEntry)Constants.keyStore.getEntry(Constants.KEY_ALIAS, null);
            Constants.secretKey = secretKeyEntry.getSecretKey();
        }

        // separate IV from data
        byte[] ivToPass = new byte[Constants.IV_LEN];
        byte[] dataToDecrypt = new byte[dataToDecryptWhole.length-Constants.IV_LEN];
        int counter = 0;
        for (int i = 0; i < Constants.IV_LEN; i++) {
            ivToPass[i] = dataToDecryptWhole[i];
        }
        for (int i = Constants.IV_LEN; i < dataToDecryptWhole.length; i++) {
            dataToDecrypt[counter++] = dataToDecryptWhole[i];
        }

        // decrypt and convert back to string
        Cipher cipherDecrypt = Cipher.getInstance(Constants.AES_SETTINGS);
        cipherDecrypt.init(Cipher.DECRYPT_MODE, Constants.secretKey, new GCMParameterSpec(Constants.GCM_TLEN, ivToPass));
        return cipherDecrypt.doFinal(dataToDecrypt);
    }

    public static void generateKeyPair(Context cxt) throws NoSuchAlgorithmException, CertificateException, InvalidKeyException, KeyStoreException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, UnrecoverableEntryException, IOException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(Constant.AES);
        keyGenerator.init(256);

        byte[] key = keyGenerator.generateKey().getEncoded();
        byte[] IV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);

        String encryptedKey = encryptWithKeyStore(key);
        String encryptedIV = encryptWithKeyStore(IV);

        SharedPreferences.Editor editor = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(cxt.getString(R.string.aes_key_pkey), encryptedKey);
        editor.putString(cxt.getString(R.string.aes_iv_pkey), encryptedIV);
        editor.commit();
    }

    public static AESPair retrieveSecretKeyFromPrefs(Context cxt) throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, UnrecoverableEntryException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, KeyStoreException, IllegalBlockSizeException {
        SharedPreferences prefs = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String keyStr = prefs.getString(cxt.getString(R.string.aes_key_pkey), null);
        String IVStr = prefs.getString(cxt.getString(R.string.aes_iv_pkey), null);

        SecretKey key = deserializeSecretKey(decryptWithKeyStore(keyStr));
        byte[] IV = decryptWithKeyStore(IVStr);

        return new AESPair(key,IV);
    }

    public static String encryptWithKey(Context cxt, String plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, CertificateException, UnrecoverableEntryException, KeyStoreException, IOException {
        byte[] plainTextMessage = plaintext.getBytes(Constants.CharSet);

        AESPair aesPair = retrieveSecretKeyFromPrefs(cxt);

        byte[] encrypted = encryptWithKeyHelper(plainTextMessage, aesPair.getKey(), aesPair.getIV());

        return android.util.Base64.encodeToString(encrypted,0);
    }

    public static String[] encryptWithKeyBatch(Context cxt, List<String> plaintexts) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, CertificateException, UnrecoverableEntryException, KeyStoreException, IOException {
        AESPair aesPair = retrieveSecretKeyFromPrefs(cxt);
        String[] out = new String[plaintexts.size()];
        int counter = 0;
        for (String plaintext : plaintexts) {
            byte[] plainTextMessage = plaintext.getBytes(Constants.CharSet);

            byte[] encrypted = encryptWithKeyHelper(plainTextMessage, aesPair.getKey(), aesPair.getIV());

            out[counter++] = android.util.Base64.encodeToString(encrypted,0);
        }
        return out;
    }

    private static byte[] encryptWithKeyHelper(byte[] plainText, SecretKey key, byte[] IV) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(Constants.AES_SETTINGS);
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), Constant.AES);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
        byte[] cipherText = cipher.doFinal(plainText);
        return cipherText;
    }

    public static String decryptWithKey(Context cxt, String encryptedStr) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, CertificateException, UnrecoverableEntryException, KeyStoreException, IOException {
        byte[] dataToDecrypt = android.util.Base64.decode(encryptedStr,0);

        AESPair aesPair = retrieveSecretKeyFromPrefs(cxt);

        byte[] decrypted = decryptWithKeyHelper(dataToDecrypt, aesPair.getKey(), aesPair.getIV());

        return new String(decrypted, Constants.CharSet);
    }

    public static String[] decryptWithKeyBatch(Context cxt, List<String> encryptedStrings) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, CertificateException, UnrecoverableEntryException, KeyStoreException, IOException {
        AESPair aesPair = retrieveSecretKeyFromPrefs(cxt);

        String[] out = new String[encryptedStrings.size()];

        int counter = 0;

        for (String encryptedString : encryptedStrings) {
            if (encryptedString.endsWith("\n")) {
                byte[] dataToDecrypt = android.util.Base64.decode(encryptedString, 0);

                byte[] decrypted = decryptWithKeyHelper(dataToDecrypt, aesPair.getKey(), aesPair.getIV());

                out[counter++] = new String(decrypted, Constants.CharSet);
            }
            else {
                out[counter++] = encryptedString;
            }
        }
        return out;
    }

    private static byte[] decryptWithKeyHelper(byte[] cipherText, SecretKey key, byte[] IV) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(Constants.AES_SETTINGS);
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), Constant.AES);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decryptedText = cipher.doFinal(cipherText);
        return decryptedText;
    }

    public static byte[] serializeSecretKey (SecretKey key) {
        return key.getEncoded();
    }

    public static SecretKey deserializeSecretKey (byte[] sk) {
        return new SecretKeySpec(sk, 0, sk.length, Constant.AES);
    }

}