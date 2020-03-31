package com.example.covidsafe.symptoms;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SymptomsDbRecordDao {

    @Query("SELECT * FROM symptoms_record_table")
    List<SymptomsRecord> getAllRecords();

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SymptomsRecord record);

    @Query("DELETE FROM symptoms_record_table")
    void deleteAll();

    @Query("DELETE FROM symptoms_record_table WHERE ts <= :ts_thresh")
    void deleteEarlierThan(long ts_thresh);

    @Query("SELECT * FROM symptoms_record_table ORDER BY ts DESC")
    List<SymptomsRecord> getSortedRecordsByTimestamp();

}
