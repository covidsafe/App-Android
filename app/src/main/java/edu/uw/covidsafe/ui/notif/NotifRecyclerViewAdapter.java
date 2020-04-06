package edu.uw.covidsafe.ui.notif;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import edu.uw.covidsafe.utils.Utils;

public class NotifRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    private ArrayList<String> links = new ArrayList<>();
//    private ArrayList<String> titles = new ArrayList<>();
//    private ArrayList<Object> desc = new ArrayList<>();
//    private ArrayList<Drawable> icons = new ArrayList<>();
    Context cxt;
    Activity av;
    int count = 0;

    public NotifRecyclerViewAdapter(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
//        titles.add("CDC Guidance");
//        titles.add("NYC Department of Health");
//        desc.add(cxt.getString(R.string.lipsum3));
//        desc.add(cxt.getString(R.string.lipsum3));
//        icons.add(cxt.getDrawable(R.drawable.res1));
//        icons.add(cxt.getDrawable(R.drawable.res2));
//        links.add("https://www.cdc.gov/");
//        links.add("https://www1.nyc.gov/site/doh/index.page");
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
//        ((edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter.ResourceCard) holder).title.setText((String)titles.get(position));
//        ((edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter.ResourceCard) holder).desc.setText((String)desc.get(position));
//        ((edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter.ResourceCard)holder).icon.setImageDrawable(icons.get(position));
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
//        ImageView icon;
//        TextView title;
//        TextView desc;
//        MaterialCardView card;
//        ConstraintLayout parentLayout;
        Button button;

        NotifCard(@NonNull View itemView) {
            super(itemView);
            this.button = itemView.findViewById(R.id.dismiss);
//            this.icon = itemView.findViewById(R.id.imageView3);
//            this.title = itemView.findViewById(R.id.textView);
//            this.desc = itemView.findViewById(R.id.resdesc);
//            this.card = itemView.findViewById(R.id.cdcView);
//            this.parentLayout = itemView.findViewById(R.id.parent);
        }
    }
}
