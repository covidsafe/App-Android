package edu.uw.covidsafe.ui.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.onboarding.OnboardingActivity;
import edu.uw.covidsafe.utils.Constants;

public class MoreRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();
    Context cxt;
    Activity av;

    public MoreRecyclerViewAdapter(Context cxt, Activity av, int section) {
        this.cxt = cxt;
        this.av = av;

        if (section == 0) {
            titles.add(cxt.getString(R.string.import_text));
            desc.add(cxt.getString(R.string.import_desc));
            icons.add(cxt.getDrawable(R.drawable.importicon));
        }
        else if (section == 1) {
            titles.add(cxt.getString(R.string.share_text));
            titles.add(cxt.getString(R.string.about_covidsafe));
            titles.add(cxt.getString(R.string.faq));
            icons.add(cxt.getDrawable(R.drawable.icon_share2));
            icons.add(cxt.getDrawable(R.drawable.logo2));
            icons.add(cxt.getDrawable(R.drawable.icon_faq2));
        }
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
        if (desc.size() > position) {
            ((MoreCard) holder).desc.setText(desc.get(position));
        }
        ((MoreCard)holder).icon.setImageDrawable(icons.get(position));
        if (titles.get(position).equals(cxt.getString(R.string.share_text))) {
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
        else if (titles.get(position).contains(cxt.getString(R.string.about_covidsafe))) {
            ((MoreCard)holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(av.getString(R.string.covidSiteLink)));
                    av.startActivity(browserIntent);
                }
            });
        }
        else if (titles.get(position).contains(cxt.getString(R.string.faq))) {
            ((MoreCard)holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(av.getString(R.string.covidSiteFaqLink)));
                    av.startActivity(browserIntent);
                }
            });
        }
        else if (titles.get(position).contains(cxt.getString(R.string.import_text))) {
            ((MoreCard)holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickBehavior();
                }
            });
            ((MoreCard)holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickBehavior();
                }
            });
        }
    }

    public void onClickBehavior() {
        if (!NetworkHelper.isNetworkAvailable(av)) {
            Toast.makeText(cxt,cxt.getString(R.string.network_down), Toast.LENGTH_LONG).show();
        }
        else if (ActivityCompat.checkSelfPermission(cxt, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            FragmentTransaction tx;
            if (av.getClass().toString().equals(MainActivity.class.toString())) {
                tx = ((MainActivity) av).getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(
                        R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                        R.anim.enter_right_to_left,R.anim.exit_right_to_left);
                tx.replace(R.id.fragment_container, Constants.ImportLocationHistoryFragment).commit();
            }
            else {
                tx = ((OnboardingActivity) av).getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(
                        R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                        R.anim.enter_right_to_left,R.anim.exit_right_to_left);
                tx.replace(R.id.fragment_container_onboarding, Constants.ImportLocationHistoryFragment).commit();
            }
        }
        else {
            ActivityCompat.requestPermissions(av, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class MoreCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView desc;
        MaterialCardView card;
        Button button;

        MoreCard(@NonNull View itemView) {
            super(itemView);
            this.card = itemView.findViewById(R.id.materialCardView);
            this.icon = itemView.findViewById(R.id.imageView11);
            this.title = itemView.findViewById(R.id.share);
            this.desc = itemView.findViewById(R.id.desc);
            this.button = itemView.findViewById(R.id.button);
        }
    }
}

