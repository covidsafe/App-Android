package edu.uw.covidsafe.seed_uuid;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "seed_uuid_record_table")
public class SeedUUIDRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts")
    public long ts;

    @NonNull
    @ColumnInfo(name = "seed")
    public String seed;

    @NonNull
    @ColumnInfo(name = "uuid")
    public String uuid;

    public SeedUUIDRecord(@NonNull long ts, String seed, String uuid) {
        this.ts = ts;
        this.seed = seed;
        this.uuid = uuid;
    }

    public long getTs() { return this.ts; }

    public String getSeed() { return this.seed; }

    public String getUUID() { return this.uuid; }

    public String toString() {
        return this.ts+","+this.seed+","+this.uuid.toString();
    }
}
