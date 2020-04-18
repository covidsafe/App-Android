package edu.uw.covidsafe.seed_uuid;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.uw.covidsafe.utils.CryptoUtils;

@Entity(tableName = "seed_uuid_record_table")
public class SeedUUIDRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts")
    public long ts;

    @NonNull
    @ColumnInfo(name = "seed")
    private String seedEncrypted;

    @NonNull
    @ColumnInfo(name = "uuid")
    private String uuidEncrypted;

    public SeedUUIDRecord(@NonNull long ts, String seedEncrypted, String uuidEncrypted) {
        this.ts = ts;
        this.seedEncrypted = seedEncrypted;
        this.uuidEncrypted = uuidEncrypted;
    }

    // accepts plaintext inputs
    public SeedUUIDRecord(@NonNull long ts, String seedEncrypted, String uuidEncrypted, Context cxt) {

        setTs(ts);

        if (seedEncrypted.length() > 0 && seedEncrypted.charAt(seedEncrypted.length()-1) == '\n') {
            this.seedEncrypted = seedEncrypted;
        }
        else {
            setSeed(seedEncrypted,cxt);
        }

        if (uuidEncrypted.length() > 0 && uuidEncrypted.charAt(uuidEncrypted.length()-1) == '\n') {
            this.uuidEncrypted = uuidEncrypted;
        }
        else {
            setUUID(uuidEncrypted,cxt);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // get encrypted properties
    public long getRawTs() {
        return this.ts;
    }

    public String getRawSeed() {
        return this.seedEncrypted;
    }

    public String getSeedEncrypted() {
        return this.seedEncrypted;
    }

    public String getRawUUID() {
        return this.uuidEncrypted;
    }

    public String getUuidEncrypted() {
        return this.uuidEncrypted;
    }
    ///////////////////////////////////////////////////////////////////////////
    // decrypts properties
    public String getSeed(Context cxt) {
        return CryptoUtils.decrypt(cxt, this.seedEncrypted);
    }

    public String getUUID(Context cxt) {
        return CryptoUtils.decrypt(cxt, this.uuidEncrypted);
    }
    ///////////////////////////////////////////////////////////////////////////
    // sets and encrypts properties
    public void setSeed(String seed, Context cxt) {
        this.seedEncrypted = CryptoUtils.encrypt(cxt, seed);
    }

    public void setUUID(String uuid,Context cxt) {
        this.uuidEncrypted = CryptoUtils.encrypt(cxt, uuid);
    }

    ///////////////////////////////////////////////////////////////////////////
    // sets pre-encrypted properties
    public void setTs(long ts) {
        this.ts = ts;
    }

    public void setSeedEncrypted(String seed) {
        this.seedEncrypted = seed;
    }

    public void setUUIDEncrypted(String uuid) {
        this.uuidEncrypted = uuid;
    }
    ///////////////////////////////////////////////////////////////////////////
}
