package edu.uw.covidsafe.seed_uuid;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SeedUUIDDbRecordDao {

    @Query("SELECT * FROM seed_uuid_record_table")
    List<SeedUUIDRecord> getAllRecords();

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SeedUUIDRecord record);

    @Query("DELETE FROM seed_uuid_record_table")
    void deleteAll();

    @Query("SELECT * FROM seed_uuid_record_table ORDER BY ts DESC")
    List<SeedUUIDRecord> getSortedRecordsByTimestamp();

    @Query("SELECT * FROM seed_uuid_record_table WHERE ts BETWEEN :ts1 AND :ts2 LIMIT 1")
    SeedUUIDRecord getRecordBetween(long ts1, long ts2);

    @Query("DELETE FROM seed_uuid_record_table WHERE ts <= :ts_thresh")
    void deleteEarlierThan(long ts_thresh);
}
