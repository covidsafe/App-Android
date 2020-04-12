package edu.uw.covidsafe.crypto;

import edu.uw.covidsafe.crypto.AES256;
import edu.uw.covidsafe.crypto.AESPair;
import edu.uw.covidsafe.crypto.ByteHelper;
import edu.uw.covidsafe.crypto.SHA256;

import org.junit.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class TestAES256 {
    @Test
    public void test_creation_of_AESPair() throws NoSuchAlgorithmException {
        AESPair symmetricKeyPair = AES256.generateKeyPairOld();
        SecretKey key = symmetricKeyPair.getKey();
        byte[] IV = symmetricKeyPair.getIV();
        assertEquals(IV.length, 16);
        assert (key instanceof SecretKey);
    }

    @Test
    public void testDecryption() {
        try {
            String data = "aTylpmAcXuHtdBjbkNLK//ElBH/RrWwztRILSp2dzZSH1kLfW+gYolSix2Lpca3giUN7yMMnT4b2\nHUIqn5O+Kw==";
            String out = AES256.decrypt(data);
            String expected = "590c652d-7150-4268-be08-ada6c1f75fb2";
            System.out.println(out);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void test_AES256_encryption_and_decryption() throws NoSuchAlgorithmException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            InvalidAlgorithmParameterException, NoSuchPaddingException {

        AESPair keyPair = AES256.generateKeyPairOld();
        byte[] plainTextMessage = SHA256.hash("hello this is a message");
        System.out.println("Plain text message : " +
                ByteHelper.convertBytesToHex(plainTextMessage));

        byte[] cipherTextFromPlainText = AES256.encrypt(plainTextMessage,
                keyPair.getKey(), keyPair.getIV());

        String cipherTextToHex = ByteHelper.convertBytesToHex(cipherTextFromPlainText);
        byte[] hexToByteArray = ByteHelper.hexStringToByteArray(cipherTextToHex);

        assertTrue(Arrays.equals(hexToByteArray, cipherTextFromPlainText));

        System.out.println("Encrypted Message : " +
                ByteHelper.convertBytesToHex(cipherTextFromPlainText));

        // Symmetric keys need to publish SecretKey byte[] and IV byte[] value.
        byte[] decryptedMessage = AES256.decrypt(cipherTextFromPlainText,
                keyPair.getKey(), keyPair.getIV());

        System.out.println("Decrypted Message : " +
                ByteHelper.convertBytesToHex(decryptedMessage));
        boolean messageRecovered = Arrays.equals(plainTextMessage, decryptedMessage);

        assertTrue(messageRecovered);

        AESPair rogueKeyPair = AES256.generateKeyPairOld();
        boolean messageRecoveredFail = true;
        try {
            AES256.decrypt(cipherTextFromPlainText, rogueKeyPair.getKey(), rogueKeyPair.getIV());
        } catch (Exception e) {
            messageRecoveredFail = false;
        }
        assertFalse(messageRecoveredFail);
    }

    @Test
    public void test_serialization_and_deserialization() throws NoSuchAlgorithmException {
        AESPair keyPair = AES256.generateKeyPairOld();
        SecretKey key = keyPair.getKey();
        byte[] IV = keyPair.getIV();
        byte[] sk = AES256.serializeSecretKey(key);

        List<byte[]> listofbytes = new ArrayList<>();
        listofbytes.add(sk);
        listofbytes.add(IV);

        byte[] shareBetweenUsers = ByteHelper.concat(listofbytes);
        assertEquals(shareBetweenUsers.length, 32+16);
        assertTrue(Arrays.equals(Arrays.copyOfRange(shareBetweenUsers, 0, 32), sk));
        assertTrue(Arrays.equals(Arrays.copyOfRange(shareBetweenUsers, 32, shareBetweenUsers.length), IV));

        SecretKey reconstructedKey = AES256.deserializeSecretKey(sk);
        boolean r = (key.equals(reconstructedKey));

        assertTrue(r);
    }
}
