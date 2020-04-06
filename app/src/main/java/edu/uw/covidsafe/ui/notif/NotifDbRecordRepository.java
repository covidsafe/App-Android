package edu.uw.covidsafe.ui.notif;

import android.content.Context;

import java.util.List;

public class NotifDbRecordRepository {
    private NotifDbRecordDao mRecordDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public NotifDbRecordRepository(Context application) {
        NotifDbRecordRoomDatabase db = NotifDbRecordRoomDatabase.getDatabase(application);
        mRecordDao = db.recordDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public List<NotifRecord> getAllRecords() {
        return mRecordDao.getAllRecords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public List<NotifRecord> getSortedRecords() {
        return mRecordDao.getSortedRecords();
    }

    public List<NotifRecord> getRecordsBetweenTimestamps(long ts1, long ts2) {
        return mRecordDao.getRecordsBetweenTimestamp(ts1, ts2);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(NotifRecord record) {
        NotifDbRecordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRecordDao.insert(record);
        });
    }

    public void deleteAll() {
        mRecordDao.deleteAll();
    }

    public void deleteEarlierThan(long ts_thresh) {
        mRecordDao.deleteEarlierThan(ts_thresh);
    }
}