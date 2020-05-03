package edu.uw.covidsafe.contact_trace;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {HumanRecord.class}, version = 1, exportSchema = false)
public abstract class HumanDbRecordRoomDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile HumanDbRecordRoomDatabase INSTANCE;
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
//            databaseWriteExecutor.execute(() -> {
//                // Populate the database in the background.
//                // If you want to start with more words, just add them.
//                RecordDao dao = INSTANCE.recordDao();
//                dao.deleteAll();

//                Record word = new Record(System.currentTimeMillis(), 0.04f, false);
//                dao.insert(word);
//            });
        }
    };

    static HumanDbRecordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HumanDbRecordRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            HumanDbRecordRoomDatabase.class, "human_record_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract HumanDbRecordDao recordDao();
}