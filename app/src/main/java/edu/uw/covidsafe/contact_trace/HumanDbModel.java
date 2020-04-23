package edu.uw.covidsafe.contact_trace;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.uw.covidsafe.ui.notif.NotifDbRecordRepository;
import edu.uw.covidsafe.ui.notif.NotifRecord;

public class HumanDbModel extends AndroidViewModel {
    HumanDbRecordRepository repo;
    public LiveData<List<HumanRecord>> records;
    public HumanDbModel(@NonNull Application application) {
        super(application);
        repo = new HumanDbRecordRepository(application);
        records = repo.getSortedRecords();
    }

    public LiveData<List<HumanRecord>> getAllSorted() {
        return records;
    }

    public void deleteAll() {repo.deleteAll();}
}
