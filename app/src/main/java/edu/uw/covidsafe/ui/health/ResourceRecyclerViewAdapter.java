package edu.uw.covidsafe.ui.health;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import edu.uw.covidsafe.utils.Utils;

public class ResourceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> links = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Object> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();
    Context cxt;
    Activity av;

    public ResourceRecyclerViewAdapter(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
        titles.add("CDC Guidance");
        titles.add("King County Department of Health");
        desc.add(cxt.getString(R.string.lipsum3));
        desc.add(cxt.getString(R.string.lipsum3));
        icons.add(cxt.getDrawable(R.drawable.res1));
        icons.add(cxt.getDrawable(R.drawable.kclogo));
        links.add("https://www.cdc.gov/");
        links.add("http://www.kingcounty.gov/covid");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_resource, parent, false);
        return new ResourceCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ResourceCard) holder).title.setText((String)titles.get(position));
        ((ResourceCard) holder).desc.setText((String)desc.get(position));
        ((ResourceCard)holder).icon.setImageDrawable(icons.get(position));
        ((ResourceCard)holder).card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openPhone(av, links.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ResourceCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView desc;
        MaterialCardView card;
        ConstraintLayout parentLayout;

        ResourceCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.imageView3);
            this.title = itemView.findViewById(R.id.textView);
            this.desc = itemView.findViewById(R.id.resdesc);
            this.card = itemView.findViewById(R.id.cdcView);
            this.parentLayout = itemView.findViewById(R.id.parent);
        }
    }
}
