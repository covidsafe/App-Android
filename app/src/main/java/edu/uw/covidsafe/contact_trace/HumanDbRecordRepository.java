package edu.uw.covidsafe.contact_trace;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class HumanDbRecordRepository {
    private HumanDbRecordDao mRecordDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public HumanDbRecordRepository(Context application) {
        HumanDbRecordRoomDatabase db = HumanDbRecordRoomDatabase.getDatabase(application);
        mRecordDao = db.recordDao();
//        mAllRecords = mRecordDao.getSortedRecords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public List<HumanRecord> getAllRecords() {
        return mRecordDao.getAllRecords();
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(HumanRecord record) {
        HumanDbRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRecordDao.insert(record);
        });
    }

    public void delete(String name) {
        mRecordDao.delete(name);
    }

    public LiveData<List<HumanRecord>> getSortedRecords() {
        return mRecordDao.getSortedRecords();
    }

    public void deleteAll() {
        mRecordDao.deleteAll();
    }
}