package edu.uw.covidsafe.symptoms;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SymptomDbModel extends AndroidViewModel {
    SymptomsDbRecordRepository repo;
    public LiveData<List<SymptomsRecord>> records;
    public SymptomDbModel(@NonNull Application application) {
        super(application);
        repo = new SymptomsDbRecordRepository(application);
        records = repo.getSortedRecords();
    }

    public LiveData<List<SymptomsRecord>> getAllSorted() {
        return records;
    }

    public void deleteAll() {repo.deleteAll();}
}
