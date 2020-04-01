package edu.uw.covidsafe.crypto;

import edu.uw.covidsafe.crypto.ByteHelper;
import edu.uw.covidsafe.crypto.SHA256;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestSHA256 {
    @Test
    public void test_hash_correct() throws NoSuchAlgorithmException {
        String message = "1";
        byte[] messageHash = SHA256.hash(message);

        String actualHashHex = "6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b";
        byte[] actualHash = ByteHelper.hexStringToByteArray(actualHashHex);

        boolean valid = Arrays.equals(messageHash, actualHash);
        assertTrue(valid);

        assertEquals(messageHash.length, 32);
    }
}
