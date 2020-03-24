package unused;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DbRecordDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(DbRecord record);

    @Query("DELETE FROM record_table")
    void deleteAll();

//    @Query("SELECT * from record_table ORDER BY ts DESC LIMIT 200")
//    List<DbRecord> getSortedRecords();
}
