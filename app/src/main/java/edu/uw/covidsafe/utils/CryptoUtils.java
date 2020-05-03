package edu.uw.covidsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.covidsafe.R;

import java.security.DigestException;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import edu.uw.covidsafe.crypto.AES256;
import edu.uw.covidsafe.crypto.SHA256;
import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDOpsAsyncTask;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;

public class CryptoUtils {

    // make very first seed
    public static void generateInitSeed(Context cxt, boolean forceUpdate) {
        Log.e("crypto", "generate init seed");
        String initSeed = UUID.randomUUID().toString();

        SharedPreferences prefs = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean initSeedExists = prefs.getBoolean(cxt.getString(R.string.init_key_exists_pkey), false);
        if (!initSeedExists || forceUpdate) {
            long ts = TimeUtils.getTime();
            editor.putLong(cxt.getString(R.string.most_recent_seed_timestamp_pkey), ts);
            editor.commit();

            editor.putBoolean(cxt.getString(R.string.init_key_exists_pkey), true);
            editor.commit();

            // add record with timestamp and empty uuid
            SeedUUIDRecord record = new SeedUUIDRecord(ts,
                    initSeed, "", cxt);
            Log.e("uuid", "generate initial seed");
            Log.e("uuid", "ts " + record.getRawTs() + "");
            Log.e("uuid", "seed " + record.getSeed(cxt));
            Log.e("uuid", "uuid " + record.getUUID(cxt));
            new SeedUUIDOpsAsyncTask(cxt, record).execute();
        } else {
            Log.e("crypto", "init seed already exists");
        }
    }

    // this is used by uuid generator
    // it generates enough UUIDs to fill the gap between the last time this method was called and now
    public static UUID generateSeedHelperWithMostRecent(Context context, long mostRecentSeedTimestamp) {
        Log.e("crypto","generate seed helper");
        int UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInMinutes*60*1000;

        if (Constants.DEBUG) {
            UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInSecondsDebug*1000;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm.ss aa");

        long curTime = TimeUtils.getTime();

        int numSeedsToGenerate = Math.round((curTime - mostRecentSeedTimestamp) / UUIDGenerationIntervalInMiliseconds);

        Log.e("crypto","num to generate "+numSeedsToGenerate);
        Log.e("crypto",format.format(curTime));
        Log.e("crypto",format.format(mostRecentSeedTimestamp));
        if (numSeedsToGenerate == 0 || mostRecentSeedTimestamp == 0) {
            numSeedsToGenerate = 1;
        }

        if (numSeedsToGenerate <= 0) {
            Log.e("crypto","done 1 "+format.format(TimeUtils.getTime()));
            return null;
        }

        int infectionWindowInMinutes = (Constants.DefaultInfectionWindowInDays *24*60);
        int maxSeedsToGenerate = infectionWindowInMinutes / Constants.UUIDGenerationIntervalInMinutes;
        int infectionWindowInMilliseconds = infectionWindowInMinutes*60*1000;
        if (Constants.DEBUG) {
            infectionWindowInMinutes = (Constants.DefaultInfectionWindowInDaysDebug *24*60);
            maxSeedsToGenerate = (int)(infectionWindowInMinutes / (Constants.UUIDGenerationIntervalInSecondsDebug/60.0));
            infectionWindowInMilliseconds = infectionWindowInMinutes*60*1000;
        }

        if (numSeedsToGenerate > maxSeedsToGenerate) {
            long timestampAtBeginningOfInfectionWindow = TimeUtils.getTime() - infectionWindowInMilliseconds;

            SeedUUIDDbRecordRepository repo = new SeedUUIDDbRecordRepository(context);
            repo.deleteAll();

            try {
                List<SeedUUIDRecord> beforeList = new LinkedList<>();
                for (int i = 0; i < 10; i++) {
                    beforeList = repo.getAllRecords();
                    Log.e("regen", "done with deletes " + beforeList.size());
                    Thread.sleep(1000);
                    if (beforeList.size() == 0) {
                        break;
                    }
                }
                if (beforeList.size() == 0) {
                    Log.e("crypto","done 2 "+format.format(TimeUtils.getTime()));
                    return null;
                }

                CryptoUtils.batchGenerateRecords(context, timestampAtBeginningOfInfectionWindow, maxSeedsToGenerate);

                for (int i = 0; i < 10; i++) {
                    List<SeedUUIDRecord> afterList = repo.getAllSortedRecords();
                    Log.e("regen", "done with inserts " + afterList.size()+","+maxSeedsToGenerate);
                    Thread.sleep(1000);
                    if (afterList.size() >=  maxSeedsToGenerate) {
                        Log.e("crypto","done 3 "+format.format(TimeUtils.getTime()));
                        return UUID.fromString(afterList.get(1).getUUID(context));
                    }
                }
            }
            catch(Exception e) {
                Log.e("err",e.getMessage());
            }
        }
        else {
            if (numSeedsToGenerate == 1) {
                SeedUUIDDbRecordRepository repo = new SeedUUIDDbRecordRepository(context);
                List<SeedUUIDRecord> beforeList = repo.getAllSortedRecords();

                SeedUUIDRecord mostRecentRecord = beforeList.get(0);

                String seed = mostRecentRecord.getSeed(context);
                try {
                    SeedUUIDRecord dummyRecord = generateSeedHelper(ByteUtils.uuid2bytes(UUID.fromString(seed)));
                    mostRecentRecord.setUUID(dummyRecord.getRawUUID(), context);

                    SeedUUIDRecord newRecord = new SeedUUIDRecord(
                            mostRecentRecord.getRawTs()+UUIDGenerationIntervalInMiliseconds,
                            dummyRecord.getRawSeed(),"",context);

                    new SeedUUIDOpsAsyncTask(context, mostRecentRecord).execute();
                    new SeedUUIDOpsAsyncTask(context, newRecord).execute();

                    SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(context.getString(R.string.most_recent_seed_timestamp_pkey), TimeUtils.getTime());
                    editor.commit();

                    Log.e("crypto","done 4 "+format.format(TimeUtils.getTime()));
                    return UUID.fromString(dummyRecord.getRawUUID());
                }
                catch(Exception e) {
                    Log.e("err",e.getMessage());
                }
                Log.e("crypto","done 5 "+format.format(TimeUtils.getTime()));
                return null;
            }
            else {
                Log.e("crypto","done 6 "+format.format(TimeUtils.getTime()));
                return batchFillInRecords(context, numSeedsToGenerate);
            }
        }

        Log.e("crypto","done 7 "+format.format(TimeUtils.getTime()));
        return null;
    }

    public static UUID batchFillInRecords(Context context, int numSeedsToGenerate) {
        Log.e("regen","batch generate records");
        SeedUUIDDbRecordRepository repo = new SeedUUIDDbRecordRepository(context);
        List<SeedUUIDRecord> beforeList = repo.getAllSortedRecords();
        Log.e("regen","record size "+beforeList.size());

        List<String> seedsToEncrypt = new LinkedList<>();
        List<String> uuidsToEncrypt = new LinkedList<>();
        List<SeedUUIDRecord> recordsToInsert = new LinkedList<>();

        int UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInMinutes*60*1000;
        if (Constants.DEBUG) {
            UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInSecondsDebug*1000;
        }

        Log.e("regen","interval "+UUIDGenerationIntervalInMiliseconds);

        SeedUUIDRecord mostRecentRecord = beforeList.get(0);
        byte[] seed = ByteUtils.uuid2bytes(UUID.fromString(mostRecentRecord.getSeed(context)));
        long ts = mostRecentRecord.getRawTs();

        try {
            for (int i = 0; i < numSeedsToGenerate; i++) {
                if (i%20==0) {
                    Log.e("regen", "gen " + i + "/" + numSeedsToGenerate);
                }
                SeedUUIDRecord dummyRecord = generateSeedHelper(seed);

                // update the existing record
                seedsToEncrypt.add(ByteUtils.byte2UUIDstring(seed));
                uuidsToEncrypt.add(dummyRecord.getRawUUID());
                recordsToInsert.add(mostRecentRecord);

                mostRecentRecord = new SeedUUIDRecord(
                        ts+UUIDGenerationIntervalInMiliseconds,
                        "",
                        ""
                );
                ts+=UUIDGenerationIntervalInMiliseconds;
                seed = ByteUtils.uuid2bytes(UUID.fromString(dummyRecord.getRawSeed()));
            }
        } catch (DigestException e) {
            Log.e("err",e.getMessage());
        }

        Log.e("regen","done generating "+recordsToInsert.size());

        String[] encryptedSeeds = CryptoUtils.encryptBatch(context, seedsToEncrypt);
        String[] encryptedUUIDs = CryptoUtils.encryptBatch(context, uuidsToEncrypt);
        Log.e("regen","done encrypting "+recordsToInsert.size());

        int counter = 0;
        for (SeedUUIDRecord record : recordsToInsert) {
            record.setSeedEncrypted(encryptedSeeds[counter]);
            record.setUUIDEncrypted(encryptedUUIDs[counter++]);
        }
        for (SeedUUIDRecord record : recordsToInsert) {
            try {
                Log.e("regen", "inserting " + record.ts + " regen");
                repo.insert(record);
            } catch (Exception e) {
                Log.e("regen", e.getMessage());
            }
        }

        int expectedLength = beforeList.size()+recordsToInsert.size()-1;
        try {
            for (int i = 0; i < 10; i++) {
                List<SeedUUIDRecord> afterList = repo.getAllSortedRecords();
                Log.e("regen", "done with inserts " + afterList.size());
                Thread.sleep(1000);
                if (afterList.size() >= expectedLength) {
                    SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(context.getString(R.string.most_recent_seed_timestamp_pkey), ts);
                    editor.commit();
                    return UUID.fromString(afterList.get(1).getUUID(context));
                }
            }
        }
        catch(Exception e) {
            Log.e("regen",e.getMessage());
        }
        return null;
    }


    // these two methods to be used when deleting all records and starting over
    public static void batchGenerateRecords(Context context, long ts_start, int numSeedsToGenerate) {
        batchGenerateRecords(context, ByteUtils.uuid2bytes(UUID.randomUUID()), ts_start, numSeedsToGenerate);
    }

    public static void batchGenerateRecords(Context context, byte[] initSeed, long ts_start, int numSeedsToGenerate) {
        Log.e("regen","batch generate records");
        SeedUUIDDbRecordRepository repo = new SeedUUIDDbRecordRepository(context);
        List<SeedUUIDRecord> beforeList = repo.getAllRecords();
        Log.e("regen","record size "+beforeList.size());

        List<String> seedsToEncrypt = new LinkedList<>();
        List<String> uuidsToEncrypt = new LinkedList<>();
        List<SeedUUIDRecord> recordsToInsert = new LinkedList<>();

        int UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInMinutes*60*1000;
        if (Constants.DEBUG) {
            UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInSecondsDebug*1000;
        }

        Log.e("regen","interval "+UUIDGenerationIntervalInMiliseconds);
        byte[] seed = initSeed;
        seedsToEncrypt.add(ByteUtils.byte2UUIDstring(seed));
        long ts = ts_start;

        try {
            for (int i = 0; i < numSeedsToGenerate-1; i++) {
                SeedUUIDRecord record = generateSeedHelper(seed);
                seedsToEncrypt.add(record.getRawSeed());
                uuidsToEncrypt.add(record.getRawUUID());

                record.setTs(ts);
                recordsToInsert.add(record);
                seed = ByteUtils.uuid2bytes(UUID.fromString(record.getRawSeed()));
                ts += UUIDGenerationIntervalInMiliseconds;
            }

            SeedUUIDRecord record = generateSeedHelper(seed);
            seedsToEncrypt.add(record.getRawSeed());
            uuidsToEncrypt.add("");

            record.setTs(ts);
            recordsToInsert.add(record);

        } catch (DigestException e) {
            Log.e("err",e.getMessage());
        }

        Log.e("regen","done generating "+recordsToInsert.size());

        String[] encryptedSeeds = CryptoUtils.encryptBatch(context, seedsToEncrypt);
        String[] encryptedUUIDs = CryptoUtils.encryptBatch(context, uuidsToEncrypt);
        Log.e("regen","done encrypting "+recordsToInsert.size());

        int counter = 0;
        for (SeedUUIDRecord record : recordsToInsert) {
            record.setSeedEncrypted(encryptedSeeds[counter]);
            record.setUUIDEncrypted(encryptedUUIDs[counter++]);
            try {
                Log.e("regen","inserting "+record.ts+" regen");
                repo.insert(record);
            }
            catch(Exception e) {
                Log.e("regen",e.getMessage());
            }
        }

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(context.getString(R.string.most_recent_seed_timestamp_pkey), ts);
        editor.commit();
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

    // this generates a single seed
    public static SeedUUIDRecord generateSeedHelper(Context cxt, byte[] seed) throws DigestException{
        byte[] out = new byte[32];
        SHA256.hash(seed, out);
        byte[] generatedSeedBytes = Arrays.copyOfRange(out,0,16);
        byte[] generatedIDBytes = Arrays.copyOfRange(out,16,32);

        String originalSeedStr = ByteUtils.byte2UUIDstring(seed);

        String seedStr = ByteUtils.byte2UUIDstring(generatedSeedBytes);
        String uuidStr = ByteUtils.byte2UUIDstring(generatedIDBytes);
        SeedUUIDRecord dummyRecord = new SeedUUIDRecord(
                0, seedStr, uuidStr, cxt);
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

            if (!Constants.keyStore.containsAlias(Constants.KEY_ALIAS)) {
                // Generate a key pair for encryption
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AES256.generateKeyStoreKeys();
                    AES256.generateKeyPair(cxt);
                } else {
                    throw new Exception("API is too low");
                }
            }
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
    }

    public static String decrypt(Context cxt, String data) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return AES256.decryptWithKey(cxt, data);
            } else {
                throw new Exception("API is too low");
            }
        }
        catch(Exception e) {
            Log.e("crypt",e.getMessage());
        }
        return "";
    }

    public static String encrypt(Context cxt, String data) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return AES256.encryptWithKey(cxt, data);
            } else {
                throw new Exception("API is too low");
            }
        }
        catch(Exception e) {
            Log.e("crypt",e.getMessage());
        }
        return "";
    }

    public static String[] encryptBatch(Context cxt, List<String> data) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return AES256.encryptWithKeyBatch(cxt, data);
            } else {
                throw new Exception("API is too low");
            }
        }
        catch(Exception e) {
            Log.e("crypt",e.getMessage());
        }
        return null;
    }

    public static String[] decryptBatch(Context cxt, List<String> data) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return AES256.decryptWithKeyBatch(cxt, data);
            } else {
                throw new Exception("API is too low");
            }
        }
        catch(Exception e) {
            Log.e("crypt",e.getMessage());
        }
        return null;
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
