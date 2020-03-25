package com.example.covidsafe.ble;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {BleRecord.class}, version = 1, exportSchema = false)
public abstract class BleDbRecordRoomDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile BleDbRecordRoomDatabase INSTANCE;
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

    static BleDbRecordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BleDbRecordRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context,
                            BleDbRecordRoomDatabase.class, "ble_record_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract BleDbRecordDao recordDao();
}