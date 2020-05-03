package edu.uw.covidsafe.gps;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GpsDbModel extends AndroidViewModel {
    GpsDbRecordRepository repo;
    public LiveData<List<GpsRecord>> records;
    public GpsDbModel(@NonNull Application application) {
        super(application);
        repo = new GpsDbRecordRepository(application);
        records = repo.getSortedRecords();
    }

    public LiveData<List<GpsRecord>> getAllSorted() {
        return records;
    }

    public void deleteAll() {repo.deleteAll();}
}
