package edu.uw.covidsafe.ui.notif;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NotifDbModel extends AndroidViewModel {
    NotifDbRecordRepository repo;
    public LiveData<List<NotifRecord>> records;
    public NotifDbModel(@NonNull Application application) {
        super(application);
        repo = new NotifDbRecordRepository(application);
        records = repo.getSortedRecords();
    }

    public LiveData<List<NotifRecord>> getAllSorted() {
        return records;
    }

    public void deleteAll() {repo.deleteAll();}
}
