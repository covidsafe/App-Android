package com.example.covidsafe.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface GpsDbRecordDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(GpsDbRecord record);

    @Query("DELETE FROM gps_record_table")
    void deleteAll();

//    @Query("SELECT * from gps_record_table ORDER BY ts DESC LIMIT 200")
//    List<DbRecord> getSortedRecords();
}
