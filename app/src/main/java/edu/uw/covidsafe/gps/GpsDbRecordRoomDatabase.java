package edu.uw.covidsafe.gps;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {GpsRecord.class}, version = 1, exportSchema = false)
public abstract class GpsDbRecordRoomDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile GpsDbRecordRoomDatabase INSTANCE;
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

    static GpsDbRecordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GpsDbRecordRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            GpsDbRecordRoomDatabase.class, "gps_record_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract GpsDbRecordDao recordDao();
}