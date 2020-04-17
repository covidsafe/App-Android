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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import edu.uw.covidsafe.crypto.AES256;
import edu.uw.covidsafe.crypto.RSA;
import edu.uw.covidsafe.crypto.SHA256;
import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDOpsAsyncTask;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import edu.uw.covidsafe.seed_uuid.UUIDGeneratorTask;

public class CryptoUtils {

    // make very first seed
    public static SeedUUIDRecord generateInitSeed(Context context) {
        String initSeed = UUID.randomUUID().toString();

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String s = prefs.getString(context.getString(R.string.most_recent_seed_pkey),"");
        if (s.isEmpty()) {
            long ts = TimeUtils.getTime();
            editor.putString(context.getString(R.string.most_recent_seed_pkey), initSeed);
            editor.putLong(context.getString(R.string.most_recent_seed_timestamp_pkey), ts);
            editor.commit();

            // add record with timestamp and empty uuid
            SeedUUIDRecord record = new SeedUUIDRecord(ts,
                    initSeed, "");
            Log.e("uuid","generate initial seed");
            new SeedUUIDOpsAsyncTask(context, record).execute();
            return record;
        }
        return null;
    }

    // this is used by uuid generator
    // it generates enough UUIDs to fill the gap between the last time this method was called and now
    public static UUID generateSeedHelper(Context context, byte[] seed, long mostRecentSeedTimestamp) {
        int UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInMinutes*60*1000;

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");

        long curTime = TimeUtils.getTime();

        int numSeedsToGenerate = Math.round((curTime - mostRecentSeedTimestamp) / UUIDGenerationIntervalInMiliseconds);

        Log.e("crypto","num to generate "+numSeedsToGenerate);
        if (mostRecentSeedTimestamp == 0) {
            numSeedsToGenerate = 1;
        }

        if (numSeedsToGenerate <= 0) {
            return null;
        }

        SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);
        List<SeedUUIDRecord> records = seedUUIDRepo.getAllSortedRecords();
        if (records.size() == 0) {
            return null;
        }
        SeedUUIDRecord mostRecentRecord = records.get(0);

        try {
            String generatedSeed = "";
            String generatedUUID = null;
            long ts = mostRecentSeedTimestamp;
            for (int i = 0; i < numSeedsToGenerate; i++) {
                SeedUUIDRecord dummyRecord = generateSeedHelper(seed);

                mostRecentRecord.setUUID(dummyRecord.getUUID());
                generatedUUID = dummyRecord.getUUID();
                new SeedUUIDOpsAsyncTask(context, mostRecentRecord).execute();

                generatedSeed = dummyRecord.getSeed();

                mostRecentRecord = new SeedUUIDRecord(ts,
                        generatedSeed, "");

                ts += UUIDGenerationIntervalInMiliseconds;
            }
            new SeedUUIDOpsAsyncTask(context, mostRecentRecord).execute();

            Log.e("crypto","setting new preference time "+generatedSeed+","+ts);
            SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(context.getString(R.string.most_recent_seed_pkey), generatedSeed);
            editor.putLong(context.getString(R.string.most_recent_seed_timestamp_pkey), curTime);
            editor.commit();
            Log.e("crypto","done setting new preference time");

            Log.e("crypto","returning most recent record" +mostRecentRecord.getSeed()+","+mostRecentRecord.getUUID());
            return UUID.fromString(generatedUUID);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }

        return UUID.randomUUID();
    }

    // this generates a single seed
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

    // used by healthy users to calculate all UUIDs they may have been exposed to
    public static List<String> chainGenerateUUIDFromSeed(String s, int numSeedsToGenerate) {
        byte[] seed = ByteUtils.string2byteArray(s);
        ArrayList<String> uuids = new ArrayList<>();
        try {
            for (int i = 0; i < numSeedsToGenerate; i++) {
                String[] record = generateSeedChainHelper(seed);
                seed = ByteUtils.string2byteArray(record[0]);
                uuids.add(record[1]);
            }
        }
        catch(Exception e) {
            Log.e("exception",e.getMessage());
        }
        return uuids;
    }

    private static String[] generateSeedChainHelper(byte[] seed) throws DigestException{
        byte[] out = new byte[32];
        SHA256.hash(seed, out);
        byte[] generatedSeedBytes = Arrays.copyOfRange(out,0,16);
        byte[] generatedIDBytes = Arrays.copyOfRange(out,16,32);
        return new String[] {ByteUtils.byte2UUIDstring(generatedSeedBytes),
                ByteUtils.byte2UUIDstring(generatedIDBytes)};
    }

    public static void keyInit(Context cxt) {
        try {
            Constants.keyStore = KeyStore.getInstance(Constants.KEY_PROVIDER);
            Constants.keyStore.load(null);
            // Generate the RSA key pairs
            if (!Constants.keyStore.containsAlias(Constants.KEY_ALIAS)) {
                // Generate a key pair for encryption
                // use symmetric if version is high enough, else assymetric
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AES256.generateKeyPair();
                } else {
                    RSA.generateKeyPair(cxt);
                }
            }
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
    }

    public static String decrypt(String data) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return AES256.decrypt(data);
//                return data;
            } else {
                return RSA.decrypt(data);
            }
        }
        catch(Exception e) {
            Log.e("crypt",e.getMessage());
        }
        return "";
    }

    public static String encrypt(String data) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return AES256.encrypt(data);
//                return data;
            } else {
                return RSA.encrypt(data);
            }
        }
        catch(Exception e) {
            Log.e("crypt",e.getMessage());
        }
        return "";
    }

//    private static Key getSecretKey(Context context) throws Exception{
//        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
//        String enryptedKeyB64 = pref.getString(ENCRYPTED_KEY, null);
//        // need to check null, omitted here
//        byte[] encryptedKey = Base64.decode(enryptedKeyB64, Base64.DEFAULT);
//        byte[] key = rsaDecrypt(encryptedKey);
//        return new SecretKeySpec(key, "AES");
//    }
//
//    private static String encryptHelper(Context context, byte[] input) {
//        String encryptedBase64Encoded="";
//        try {
//            Cipher c = Cipher.getInstance(AES_MODE, "BC");
//            c.init(Cipher.ENCRYPT_MODE, getSecretKey(context));
//            byte[] encodedBytes = c.doFinal(input);
//            encryptedBase64Encoded = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
//        }
//        catch(Exception e) {
//            Log.e("logme",e.getMessage());
//        }
//        return encryptedBase64Encoded;
//    }
//
//    private static byte[] decryptHelper(Context context, String encryptedB64) {
//        byte[] encryptedKey = Base64.decode(encryptedB64, Base64.DEFAULT);
//        byte[] decodedBytes=null;
//        try {
//            Cipher c = Cipher.getInstance(AES_MODE, "BC");
//            c.init(Cipher.DECRYPT_MODE, getSecretKey(context));
//            decodedBytes = c.doFinal(encryptedKey);
//        }
//        catch(Exception e) {
//            Log.e("logme",e.getMessage());
//        }
//        return decodedBytes;
//    }
}
