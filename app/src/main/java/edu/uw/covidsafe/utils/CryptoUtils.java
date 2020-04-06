package edu.uw.covidsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.example.covidsafe.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.DigestException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import edu.uw.covidsafe.crypto.SHA256;
import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDOpsAsyncTask;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;

public class CryptoUtils {

    // make very first seed
    public static void generateInitSeed(Context context, boolean forceRefresh) {
        String initSeed = UUID.randomUUID().toString();

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String s = prefs.getString(context.getString(R.string.most_recent_seed_pkey),"");
        if (s.isEmpty() || forceRefresh) {
            editor.putString(context.getString(R.string.most_recent_seed_pkey), initSeed);
            editor.commit();

            // add record with timestamp and empty uuid
            SeedUUIDRecord record = new SeedUUIDRecord(System.currentTimeMillis(),
                    initSeed, "");
            Log.e("uuid","generate initial seed");
            new SeedUUIDOpsAsyncTask(context, record).execute();
        }
    }

    // chain generation for potentially exposed users
    public static SeedUUIDRecord generateSeedHelper(byte[] seed) throws DigestException{
        byte[] out = new byte[32];
        SHA256.hash(seed, out);
        byte[] generatedSeedBytes = Arrays.copyOfRange(out,0,16);
        byte[] generatedIDBytes = Arrays.copyOfRange(out,16,32);
        SeedUUIDRecord dummyRecord = new SeedUUIDRecord(
                0, ByteUtils.byte2UUIDstring(generatedSeedBytes),
                ByteUtils.byte2UUIDstring(generatedIDBytes));
        return dummyRecord;
    }

    // generation ith seed
    public static SeedUUIDRecord generateSeed(Context context, byte[] seed) {
        try {
            SeedUUIDRecord dummyRecord = generateSeedHelper(seed);

            // get the most recent seed/uuid pair
            // fill in the uuid, put back into DB
            SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);
            SeedUUIDRecord mostRecentRecord = seedUUIDRepo.getAllSortedRecords().get(0);
            mostRecentRecord.uuid =  dummyRecord.uuid;
            new SeedUUIDOpsAsyncTask(context, mostRecentRecord).execute();

            // create the next record with the generated seed
            // return this and store in DB if necessary
            String generatedSeed = dummyRecord.seed;
            SeedUUIDRecord nextRecord = new SeedUUIDRecord(System.currentTimeMillis(),
                    generatedSeed, "");

            new SeedUUIDOpsAsyncTask(context, nextRecord).execute();

            SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(context.getString(R.string.most_recent_seed_pkey), generatedSeed);
            editor.commit();

            return mostRecentRecord;
        }
        catch(DigestException e) {
            Log.e("error",e.getMessage());
        }
        return null;
    }

    public static List<String> chainGenerateUUIDFromSeed(String s, int numSeedsToGenerate) {
        byte[] seed = ByteUtils.string2byteArray(s);
        ArrayList<String> uuids = new ArrayList<>();
        try {
            for (int i = 0; i < numSeedsToGenerate; i++) {
                SeedUUIDRecord record = generateSeedHelper(seed);
                seed = ByteUtils.string2byteArray(record.seed);
                uuids.add(record.uuid);
            }
        }
        catch(Exception e) {
            Log.e("exception",e.getMessage());
        }
        return uuids;
    }

    public static String encryptTimestamp(Context cxt, long ts) {
        return encryptHelper(cxt, ByteUtils.longToBytes(ts));
    }

    public static long decryptTimestamp(Context cxt, String encryptedB64) {
        byte[] bb = decryptHelper(cxt, encryptedB64);
        if (bb == null) {
            return 0;
        }
        return ByteUtils.bytesToLong(bb);
    }

    static KeyStore keyStore;
    public static void keyInit(Context cxt) {
        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);
            // Generate the RSA key pairs
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                // Generate a key pair for encryption
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(cxt)
                        .setAlias(KEY_ALIAS)
                        .setSubject(new X500Principal("CN=" + KEY_ALIAS))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AndroidKeyStore);
                kpg.initialize(spec);
                kpg.generateKeyPair();
                genAESKey(cxt);
            }
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
    }

    static String KEY_ALIAS = "KEY";
    static String AndroidKeyStore = "AndroidKeyStore";
    static String RSA_MODE =  "RSA/ECB/PKCS1Padding";

    private static byte[] rsaEncrypt(byte[] secret) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
        // Encrypt the text
        Cipher inputCipher;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
            inputCipher=Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
        }
        else { // android m and above
            inputCipher=Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();
        return vals;
    }

    private static byte[] rsaDecrypt(byte[] encrypted) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(KEY_ALIAS, null);
        Cipher output;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
            output=Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
        }
        else { // android m and above
            output=Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }

        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(encrypted), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte)nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i).byteValue();
        }
        return bytes;
    }

    private static String ENCRYPTED_KEY = "key";
    private static void genAESKey(Context cxt) {
        SharedPreferences pref = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);

        // let's generate the key to be stored in our prefs
        if (enryptedKeyB64 == null) {
            byte[] key = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(key);
            try {
                byte[] encryptedKey = rsaEncrypt(key);
                enryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString(ENCRYPTED_KEY, enryptedKeyB64);
                edit.commit();
            }
            catch(Exception e) {
                Log.e("logme",e.getMessage());
            }
        }
    }

    private static final String AES_MODE = "AES/ECB/PKCS7Padding";
    private static Key getSecretKey(Context context) throws Exception{
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);
        // need to check null, omitted here
        byte[] encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT);
        byte[] key = rsaDecrypt(encryptedKey);
        return new SecretKeySpec(key, "AES");
    }

    private static String encryptHelper(Context context, byte[] input) {
        String encryptedBase64Encoded="";
        try {
            Cipher c = Cipher.getInstance(AES_MODE, "BC");
            c.init(Cipher.ENCRYPT_MODE, getSecretKey(context));
            byte[] encodedBytes = c.doFinal(input);
            encryptedBase64Encoded = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
        return encryptedBase64Encoded;
    }

    private static byte[] decryptHelper(Context context, String encryptedB64) {
        byte[] encryptedKey = Base64.decode(encryptedB64, Base64.DEFAULT);
        byte[] decodedBytes=null;
        try {
            Cipher c = Cipher.getInstance(AES_MODE, "BC");
            c.init(Cipher.DECRYPT_MODE, getSecretKey(context));
            decodedBytes = c.doFinal(encryptedKey);
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
        return decodedBytes;
    }
}
