package edu.uw.covidsafe.gps;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GpsDbRecordDao {

    @Query("SELECT * from gps_record_table")
    List<GpsRecord> getAllRecords();

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(GpsRecord record);

    @Query("DELETE FROM gps_record_table")
    void deleteAll();

    @Query("DELETE FROM gps_record_table WHERE ts <= :ts_thresh")
    void deleteEarlierThan(long ts_thresh);

    @Query("SELECT * from gps_record_table ORDER BY ts")
    List<GpsRecord> getSortedRecords();
}
