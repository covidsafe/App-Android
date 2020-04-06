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


public class NotifRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context cxt;
    Activity av;
    int count = 0;
    ArrayList<String> messages = new ArrayList<String>();

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

    public void notifyUser(String msg) {
        if (count == 0) {
            this.messages.add(msg);
            notifyItemInserted(count);
            count += 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((NotifCard)holder).button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("RECYCLER","RECYCLER POS "+position);
                if (position < messages.size()) {
                    notifyItemRemoved(position);
                    messages.remove(position);
                    count -= 1;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class NotifCard extends RecyclerView.ViewHolder {
        Button button;
        TextView header;
        TextView message;
        NotifCard(@NonNull View itemView) {
            super(itemView);
            this.button = itemView.findViewById(R.id.dismiss);
            this.header = itemView.findViewById(R.id.textView11);
            this.message = itemView.findViewById(R.id.textView12);
        }
    }
}
