package edu.uw.covidsafe.crypto;

import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class TestBase64Conversions {
    @Test
    public void test_base64_conversions() {
        String message = "hello this is a message";
        byte[] messagebytes = message.getBytes();

        byte[] encodedNormal = Base64.getEncoder().encode(messagebytes);
        byte[] encodedApache = org.apache.commons.codec.binary.Base64.encodeBase64(messagebytes);

        boolean r = Arrays.equals(encodedNormal, encodedApache);

        String encodedNormalString = Base64.getEncoder().encodeToString(messagebytes);
        String encodedApacheString = org.apache.commons.codec.binary.Base64.encodeBase64String(messagebytes);

        assertTrue(r);
        assertEquals(encodedNormalString, encodedApacheString);

        // INTEROPERATE
        byte[] messageNormalDecode = Base64.getDecoder().decode(encodedNormalString);
        byte[] messageNormalApacheDecode = org.apache.commons.codec.binary.Base64.decodeBase64(encodedNormalString);

        byte[] messageApacheNormalDecode = Base64.getDecoder().decode(encodedApacheString);
        byte[] messageApacheDecode = org.apache.commons.codec.binary.Base64.decodeBase64(encodedApacheString);

        assertArrayEquals(messageNormalDecode, messageNormalApacheDecode);
        assertArrayEquals(messageApacheDecode, messageApacheNormalDecode);
    }
}
