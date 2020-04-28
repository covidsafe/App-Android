package edu.uw.covidsafe.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import edu.uw.covidsafe.utils.Utils;

public class MoreRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();
    Context cxt;
    Activity av;

    public MoreRecyclerViewAdapter(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
        titles.add(cxt.getString(R.string.share_text));
        titles.add(cxt.getString(R.string.about_covidsafe));
        titles.add(cxt.getString(R.string.faq));
        icons.add(cxt.getDrawable(R.drawable.icon_share2));
        icons.add(cxt.getDrawable(R.drawable.logo2));
        icons.add(cxt.getDrawable(R.drawable.icon_faq2));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_more, parent, false);
        return new MoreCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MoreCard)holder).title.setText(titles.get(position));
//        ((MoreCard)holder).desc.setText(desc.get(position));
        ((MoreCard)holder).icon.setImageDrawable(icons.get(position));
        if (titles.get(position).equals(cxt.getString(R.string.share_text).toLowerCase())) {
            ((MoreCard)holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CovidSafe");
                    String shareMessage= av.getString(R.string.learn_about_us)+
                            av.getString(R.string.covidSiteLink);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    av.startActivity(Intent.createChooser(shareIntent, av.getString(R.string.select_share)));
                }
            });
        }
        else if (titles.get(position).toLowerCase().contains(cxt.getString(R.string.about_covidsafe).toLowerCase())) {
            ((MoreCard)holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(av.getString(R.string.covidSiteLink)));
                    av.startActivity(browserIntent);
                }
            });
        }
        else if (titles.get(position).toLowerCase().contains(cxt.getString(R.string.faq).toLowerCase())) {
            ((MoreCard)holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(av.getString(R.string.covidSiteFaqLink)));
                    av.startActivity(browserIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class MoreCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
//        TextView desc;
        MaterialCardView card;

        MoreCard(@NonNull View itemView) {
            super(itemView);
            this.card = itemView.findViewById(R.id.materialCardView);
            this.icon = itemView.findViewById(R.id.imageView11);
            this.title = itemView.findViewById(R.id.share);
//            this.desc = itemView.findViewById(R.id.perm1desc);
        }
    }
}

