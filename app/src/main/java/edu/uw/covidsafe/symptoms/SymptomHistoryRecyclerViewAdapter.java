package edu.uw.covidsafe.symptoms;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.symptoms.SymptomsOpsAsyncTask;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.notif.NotifRecord;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

public class SymptomHistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    View view;
    int historySize = 0;
    List<SymptomsRecord> records = new LinkedList<SymptomsRecord>();
    Map<Integer, List<SymptomsRecord>> timestampToHistoryIndex = new HashMap<Integer,List<SymptomsRecord>>();

    public SymptomHistoryRecyclerViewAdapter(Context mContext, View view) {
        this.mContext = mContext;
        this.view = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_symptom_log_recent, parent, false);
        return new SymptomHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SymptomHistoryHolder)holder).amStatus.setText(R.string.not_logged_txt);
        ((SymptomHistoryHolder)holder).pmStatus.setText(R.string.not_logged_text);
        SimpleDateFormat outformat = new SimpleDateFormat("h:mm aa");
        SimpleDateFormat outformat2 = new SimpleDateFormat("MMMM dd, yyyy");

//        ((SymptomHistoryHolder)holder).dayAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SymptomUtils.editAction(mContext, av, view, );
//            }
//        });
        ((SymptomHistoryHolder)holder).dayAction.setVisibility(View.GONE);

        List<SymptomsRecord> records = timestampToHistoryIndex.get(position);
        ((SymptomHistoryHolder)holder).day.setText(outformat2.format(records.get(0).getTs()));
        SimpleDateFormat ampm = new SimpleDateFormat("aa");
        for (SymptomsRecord record : records) {
            Date date = new Date(record.getLogTime());
            if (ampm.format(date).toLowerCase().equals("am")) {
                ((SymptomHistoryHolder)holder).amStatus.setText(mContext.getString(R.string.logged_txt)+": "+outformat.format(date));
            }
            else if (ampm.format(date).toLowerCase().equals("pm")) {
                ((SymptomHistoryHolder)holder).pmStatus.setText(mContext.getString(R.string.logged_txt)+": "+outformat.format(date));
            }
        }
    }

    public void setRecords(List<SymptomsRecord> records, View view) {
        this.records = records;

        Set<String> dates = new HashSet<>();
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String today = format.format(new Date());

        for (SymptomsRecord record : this.records) {
            String date = format.format(new Date(record.getTs()));
            if (!dates.contains(date) && !date.equals(today)) {
                dates.add(date);
            }
        }
        this.historySize = dates.size();

        timestampToHistoryIndex = new HashMap<>();
        String currentDate = "";
        int currentIndex = -1;
        for (SymptomsRecord record : this.records) {
            String date = format.format(new Date(record.getTs()));
            if (!date.equals(today)) {
                if (!date.equals(currentDate)) {
                    currentIndex += 1;
                    timestampToHistoryIndex.put(currentIndex, new LinkedList<>());
                    timestampToHistoryIndex.get(currentIndex).add(record);
                    currentDate = date;
                } else {
                    timestampToHistoryIndex.get(currentIndex).add(record);
                }
            }
        }

        Log.e("symptoms","data has changed" +this.historySize);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // each record is for am/pm
        // but we combine the am/pm into one display
        return historySize;
    }

    public class SymptomHistoryHolder extends RecyclerView.ViewHolder {
        TextView amStatus;
        TextView pmStatus;
        ImageView dayAction;
        TextView day;

        SymptomHistoryHolder(@NonNull View itemView) {
            super(itemView);
            this.amStatus = itemView.findViewById(R.id.amStatus);
            this.pmStatus = itemView.findViewById(R.id.pmStatus);
            this.dayAction = itemView.findViewById(R.id.dayAction);
            this.day = itemView.findViewById(R.id.day);
        }
    }
}
