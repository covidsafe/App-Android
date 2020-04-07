package edu.uw.covidsafe.ui.notif;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotifDbRecordDao {

    @Query("SELECT * from notif_record_table")
    List<NotifRecord> getAllRecords();

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NotifRecord record);

    @Query("DELETE FROM notif_record_table")
    void deleteAll();

    @Query("DELETE FROM notif_record_table WHERE ts_start <= :ts_thresh")
    void deleteEarlierThan(long ts_thresh);

    @Query("SELECT * from notif_record_table ORDER BY ts_start DESC LIMIT 5")
    LiveData<List<NotifRecord>> getSortedRecords();

    @Query("SELECT * from notif_record_table WHERE ts_start BETWEEN :ts1 AND :ts2")
    List<NotifRecord> getRecordsBetweenTimestamp(long ts1, long ts2);
}
