package edu.uw.covidsafe.ui.notif;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context cxt;
    Activity av;
    List<NotifRecord> records = new ArrayList<>();

    public HistoryRecyclerViewAdapter(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history, parent, false);
        return new HistoryCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotifRecord notif = records.get(position);
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm");
        SimpleDateFormat timeFormat2 = new SimpleDateFormat("h:mma");
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d");
        String tt = timeFormat.format(notif.getTs_start()) +"-"+ timeFormat2.format(notif.getTs_end());
        ((HistoryCard) holder).time.setText(tt);
        ((HistoryCard) holder).msg.setText(notif.msg);
        ((HistoryCard) holder).date.setText(dateFormat.format(notif.getTs_start()));
    }

    public void setRecords(List<NotifRecord> records, View view) {
        TextView tv = (TextView)view.findViewById(R.id.historyTitle);
        if (records.size() == 0) {
            av.runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("");
                    tv.setVisibility(View.GONE);
                }});
        }
        else {
            av.runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText(cxt.getResources().getString(R.string.exposure_history));
                    tv.setVisibility(View.VISIBLE);
                }});
        }

        if (records.size() > this.records.size()) {
            Log.e("notif","history item inserted");
            notifyItemInserted(0);
        }
        else {
            Log.e("notif","history dataset changed");
            notifyDataSetChanged();
        }
        this.records = records;
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class HistoryCard extends RecyclerView.ViewHolder {
        TextView time;
        TextView date;
        TextView msg;
        HistoryCard(@NonNull View itemView) {
            super(itemView);
            this.time = itemView.findViewById(R.id.time);
            this.date = itemView.findViewById(R.id.date);
            this.msg = itemView.findViewById(R.id.messageView);
        }
    }
}
