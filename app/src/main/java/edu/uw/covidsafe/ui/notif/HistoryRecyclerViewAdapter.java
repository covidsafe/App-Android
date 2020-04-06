package edu.uw.covidsafe.ui.notif;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.util.ArrayList;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context cxt;
    Activity av;
    int count = 0;

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

    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class HistoryCard extends RecyclerView.ViewHolder {
        TextView time;
        TextView date;
        HistoryCard(@NonNull View itemView) {
            super(itemView);
            this.time = itemView.findViewById(R.id.time);
            this.date = itemView.findViewById(R.id.date);
        }
    }
}
