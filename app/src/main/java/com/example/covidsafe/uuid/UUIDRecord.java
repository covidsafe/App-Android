package com.example.covidsafe.uuid;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "uuid_record_table")
public class UUIDRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts")
    private long ts;

    @NonNull
    @ColumnInfo(name = "uuid")
    public String uuid;

    public UUIDRecord(@NonNull long ts, String uuid) {
        this.ts = ts;
        this.uuid = uuid;
    }

    public long getTs() { return this.ts; }

    public String getUUID() { return this.uuid; }

    public String toString() {
        return this.ts+","+this.uuid.toString();
    }
}
