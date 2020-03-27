package com.example.covidsafe.uuid;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.covidsafe.ble.BleRecord;

import java.util.List;

@Dao
public interface UUIDDbRecordDao {

    @Query("SELECT * FROM uuid_record_table")
    List<UUIDRecord> getAllRecords();

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UUIDRecord record);

    @Query("DELETE FROM uuid_record_table")
    void deleteAll();

    @Query("SELECT * FROM uuid_record_table ORDER BY ts DESC")
    List<UUIDRecord> getSortedRecordsByTimestamp();

}
