package com.example.covidsafe.uuid;

import android.content.Context;

import java.util.List;

public class UUIDDbRecordRepository {
    private UUIDDbRecordDao mRecordDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public UUIDDbRecordRepository(Context application) {
        UUIDDbRecordRoomDatabase db = UUIDDbRecordRoomDatabase.getDatabase(application);
        mRecordDao = db.recordDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public List<UUIDRecord> getAllRecords() {
        return mRecordDao.getAllRecords();
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(UUIDRecord record) {
        UUIDDbRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRecordDao.insert(record);
        });
    }
}