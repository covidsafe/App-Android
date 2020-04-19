package edu.uw.covidsafe.symptoms;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import edu.uw.covidsafe.ui.notif.NotifRecord;

public class SymptomsDbRecordRepository {
    private SymptomsDbRecordDao mRecordDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public SymptomsDbRecordRepository(Context application) {
        SymptomsDbRecordRoomDatabase db = SymptomsDbRecordRoomDatabase.getDatabase(application);
        mRecordDao = db.recordDao();
//        mAllRecords = mRecordDao.getSortedRecords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    public LiveData<List<SymptomsRecord>> getSortedRecords() {
        return mRecordDao.getSortedRecordsByTimestamp();
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(SymptomsRecord record) {
        SymptomsDbRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRecordDao.insert(record);
        });
    }

    public void deleteAll() {
        mRecordDao.deleteAll();
    }

    public void delete(long ts) {
        mRecordDao.delete(ts);
    }

    public void deleteEarlierThan(long ts_thresh) {
        mRecordDao.deleteEarlierThan(ts_thresh);
    }
}