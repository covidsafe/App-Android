package com.example.covidsafe.crypto;

import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestRSA {
    @Test
    public void test_2048bit_RSA_signatures() throws Exception {
        String message = "1";

        KeyPair kp2048 = RSA.generateKeyPair();
        PublicKey pk = kp2048.getPublic();
        PrivateKey sk = kp2048.getPrivate();

        byte[] sign = RSA.signAsBytes(message, sk);
        assertEquals(sign.length, 256);

        boolean valid = RSA.verifyAsBytes(message, sign, pk);
        assertTrue(valid);
    }

    @Test
    public void test_4096bit_RSA_signatures() throws Exception {
        String message = "1";
        KeyPair kp4096 = RSA.generateKeyPair(4096);
        PublicKey pk = kp4096.getPublic();
        PrivateKey sk = kp4096.getPrivate();

        byte[] sign = RSA.signAsBytes(message, sk);
        assertEquals(sign.length, 512);

        boolean valid = RSA.verifyAsBytes(message, sign, pk);
        assertTrue(valid);
    }

    @Test
    public void test_2048bit_RSA_encryption() throws Exception {
        KeyPair alice = RSA.generateKeyPair();
        KeyPair bob = RSA.generateKeyPair();

        byte[] message = SHA256.hash("hello");

        byte[] encryptedMessageFromAliceToBob = RSA.encrypt(message, bob.getPublic());
        byte[] encryptedMessageFromBobToAlice = RSA.encrypt(message, alice.getPublic());

        byte[] bobDecryptsAlicesMessage = RSA.decrypt(encryptedMessageFromAliceToBob, bob.getPrivate());
        boolean bobCanReadAlicesMessage = Arrays.equals(bobDecryptsAlicesMessage, message);

        System.out.println("Bob can read Alice's message : " + bobCanReadAlicesMessage);
        assertTrue(bobCanReadAlicesMessage);

        byte[] aliceDecryptsBobsMessage = RSA.decrypt(encryptedMessageFromBobToAlice, alice.getPrivate());
        boolean aliceCanReadBobsMessage = Arrays.equals(aliceDecryptsBobsMessage, message);

        System.out.println("Alice can read Bob's message : " + aliceCanReadBobsMessage);
        assertTrue(aliceCanReadBobsMessage);
    }

    @Test(expected = BadPaddingException.class)
    public void test_RSA_decryption_failures() throws Exception {
        KeyPair alice = RSA.generateKeyPair();
        KeyPair bob = RSA.generateKeyPair();

        KeyPair eve = RSA.generateKeyPair();

        byte[] message = SHA256.hash("hello");

        byte[] encryptedMessageFromAliceToBob = RSA.encrypt(message, bob.getPublic());

        byte[] eveTriesToDecryptMessage = RSA.decrypt(encryptedMessageFromAliceToBob, eve.getPrivate());
    }
}
