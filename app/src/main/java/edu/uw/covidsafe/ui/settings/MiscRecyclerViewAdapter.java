package edu.uw.covidsafe.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MiscRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();
    Context cxt;
    Activity av;

    public MiscRecyclerViewAdapter(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
        titles.add(cxt.getString(R.string.legal_text));
        titles.add(cxt.getString(R.string.privacy_text));
        titles.add(cxt.getString(R.string.terms_text));
        titles.add(cxt.getString(R.string.release_text));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_misc, parent, false);
        return new MiscCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MiscCard)holder).title.setText(titles.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class MiscCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView desc;
        MaterialCardView card;

        MiscCard(@NonNull View itemView) {
            super(itemView);
//            this.card = itemView.findViewById(R.id.materialCardView);
//            this.icon = itemView.findViewById(R.id.imageView11);
            this.title = itemView.findViewById(R.id.perm1);
//            this.desc = itemView.findViewById(R.id.perm1desc);
        }
    }
}

