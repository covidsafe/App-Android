package edu.uw.covidsafe.contact_trace;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HumanDbRecordDao {

    @Query("SELECT * FROM human_record_table")
    List<HumanRecord> getAllRecords();

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HumanRecord record);

    @Query("DELETE FROM human_record_table")
    void deleteAll();

    @Query("DELETE FROM human_record_table WHERE name == :name")
    void delete(String name);

    @Query("SELECT * FROM human_record_table ORDER BY name")
    LiveData<List<HumanRecord>> getSortedRecords();
}
