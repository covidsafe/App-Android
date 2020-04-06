package edu.uw.covidsafe.ui.notif;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;


public class NotifRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context cxt;
    Activity av;
    int count = 0;

    public NotifRecyclerViewAdapter(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        return new NotifCard(view);
    }

    public void notifyUser() {
        if (count != 1) {
            count = 1;
            notifyItemInserted(0);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((NotifCard)holder).button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                notifyItemRemoved(0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class NotifCard extends RecyclerView.ViewHolder {
        Button button;

        NotifCard(@NonNull View itemView) {
            super(itemView);
            this.button = itemView.findViewById(R.id.dismiss);
        }
    }
}
