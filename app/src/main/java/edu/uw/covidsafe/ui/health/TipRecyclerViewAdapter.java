package edu.uw.covidsafe.ui.health;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

public class TipRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Object> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();
    private List<String> links = new ArrayList<>();

    private Context mContext;
    private Activity av;

    public TipRecyclerViewAdapter(Context mContext, Activity av) {
        this.mContext = mContext;
        this.av = av;
    }

    public void enableTips(int size, View view, boolean diagnosis) {
//        Log.e("tip","enabletips");
        TextView tv = view.findViewById(R.id.sick);
        if (size>=1 && titles.size()==0) {

            // for the diagnosed
            if (diagnosis) {
                titles.add("");
                desc.add(mContext.getString(R.string.call_911));
                icons.add(mContext.getDrawable(R.drawable.icon_phone3));
                links.add("");

                titles.add(mContext.getString(R.string.isolation_order));
//                desc.add(getQuarantineTime());
//                desc.add(mContext.getString(R.string.tip_desc_1));
                Spannable ss = (Spannable)Html.fromHtml(
                        mContext.getString(R.string.isolation1)+" <b>"+mContext.getString(R.string.isolation2)+"</b>");
                desc.add(ss);
                icons.add(mContext.getDrawable(R.drawable.icon_quarantine));
                links.add("https://www.kingcounty.gov/depts/health/communicable-diseases/disease-control/novel-coronavirus/quarantine.aspx");

                titles.add(mContext.getString(R.string.monitor_your_symptoms));
                Spannable ss2 = (Spannable)Html.fromHtml(
                        mContext.getString(R.string.monitor1)+" <b>"+mContext.getString(R.string.monitor2)+"</b> "+mContext.getString(R.string.monitor3)
                );
                desc.add(ss2);
                icons.add(mContext.getDrawable(R.drawable.icon_symptoms));
                links.add("https://kingcounty.gov/depts/health/communicable-diseases/disease-control/novel-coronavirus/FAQ.aspx");
            }
            else {
                // for the exposed
                titles.add("");
                desc.add(mContext.getString(R.string.call_911));
                icons.add(mContext.getDrawable(R.drawable.icon_phone3));
                links.add("");

                titles.add(mContext.getString(R.string.quarantine_directive));
                desc.add(mContext.getString(R.string.quarantine_desc));
                icons.add(mContext.getDrawable(R.drawable.icon_quarantine));
                links.add("https://www.kingcounty.gov/depts/health/communicable-diseases/disease-control/novel-coronavirus/quarantine.aspx");

                titles.add(mContext.getString(R.string.monitor_your_symptoms));
                Spannable ss = (Spannable)Html.fromHtml(
                        mContext.getString(R.string.monitor1)+" <b>"+mContext.getString(R.string.monitor2)+"</b> "+mContext.getString(R.string.monitor3)
                );
                desc.add(ss);
                icons.add(mContext.getDrawable(R.drawable.icon_symptoms));
                links.add("https://kingcounty.gov/depts/health/communicable-diseases/disease-control/novel-coronavirus/FAQ.aspx");

                titles.add(mContext.getString(R.string.request_test));
                desc.add(mContext.getString(R.string.tip_desc_2));
                icons.add(mContext.getDrawable(R.drawable.icon_test));
                links.add("https://kingcounty.gov/depts/health/communicable-diseases/disease-control/novel-coronavirus/FAQ.aspx");
            }
//            titles.add("Contact your healthcare professional");
//            desc.add("Please contact your healthcare professional for next steps.");
//            icons.add(mContext.getDrawable(R.drawable.icon_phone2));
//
//            titles.add("Isolate from those around you");
//            desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");
//            icons.add(mContext.getDrawable(R.drawable.icon_quarantine));

//            titles.add("Practice Good Hygiene");
//            desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");
//            icons.add(mContext.getDrawable(R.drawable.icon_health));

            av.runOnUiThread(new Runnable() {
                public void run() {
                    if (tv!=null) {
                        tv.setText(R.string.what_to_do_if_you_are_sick);
                        tv.setVisibility(View.VISIBLE);
                    }
                }});
//            Log.e("notif","tip changed");
            notifyDataSetChanged();
        }
        else if (size==0){
            titles.clear();
            desc.clear();
            icons.clear();
            av.runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("");
                    tv.setVisibility(View.GONE);
                }});
//            Log.e("notif","tip 0 changed");
            notifyDataSetChanged();
        }
    }

    public Spannable getQuarantineTime() {
        long thresh = TimeUtils.getNDaysForward(Constants.QuarantineLengthInDays);

        SimpleDateFormat format = new SimpleDateFormat("MMMM d");
        String ss = format.format(new Date(thresh));

        SharedPreferences prefs = mContext.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
        if (prefs.getString(mContext.getString(R.string.quarantine_end_time_pkey),"").isEmpty()) {
            editor.putString(mContext.getString(R.string.quarantine_end_time_pkey), ss);
            editor.commit();
        }
        else {
            ss = prefs.getString(mContext.getString(R.string.quarantine_end_time_pkey),"");
        }
        return (Spannable) Html.fromHtml(
                "If you start your self quarantine today, your 14 days will end <b>"+ss+"</b>. Please check with your local Health Authorities for more guidance."
        );
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
//        Log.e("tip","oncreateviewholder");
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_action, parent, false);
            RecyclerView.ViewHolder volder = new TipRecyclerViewAdapter.ActionCard(view);
//            Log.e("state","volder height "+volder.itemView.getMeasuredHeight());
            return volder;
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_call, parent, false);
            RecyclerView.ViewHolder volder = new TipRecyclerViewAdapter.CallCard(view);
//            Log.e("state","volder height "+volder.itemView.getMeasuredHeight());
            return volder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (titles.get(position).isEmpty()) {
            ((CallCard) holder).desc.setText((String)desc.get(position));
            ((CallCard)holder).icon.setImageDrawable(icons.get(position));
            ((CallCard)holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constants.PUBLIC_DEMO) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                                .setMessage(mContext.getString(R.string.demo_disabled))
                                .setPositiveButton(mContext.getString(R.string.ok),null)
                                .setCancelable(true).create();
                        dialog.show();
                    }
                    else {
                        Utils.openPhone(av, mContext.getString(R.string.phone));
                    }
                }
            });
        }
        else {
            ((ActionCard)holder).title.setText(titles.get(position));
            if (desc.get(position).getClass().toString().contains(Spannable.class.toString())) {
//                Log.e("spannable",desc.get(position).toString());
                ((ActionCard) holder).desc.setText((Spannable) desc.get(position));
            }
            else {
                ((ActionCard) holder).desc.setText((String) desc.get(position));
            }
            ((ActionCard)holder).icon.setImageDrawable(icons.get(position));

            if (!links.get(position).isEmpty()) {
                ((ActionCard) holder).whatHappens.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(links.get(position)));
                        av.startActivity(i);
                    }
                });
            }
            else {
                ((ActionCard) holder).whatHappens.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
//        Log.e("state","getItemCount "+titles.size());
        return titles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (titles.get(position).isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    public class ActionCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView desc;
        Button whatHappens;
        ConstraintLayout parentLayout;
        MaterialCardView card;

        ActionCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.title = itemView.findViewById(R.id.textView7);
            this.title.setMovementMethod(LinkMovementMethod.getInstance());
            this.desc = itemView.findViewById(R.id.textView5);
            this.whatHappens = itemView.findViewById(R.id.whatHappens);
            this.parentLayout = itemView.findViewById(R.id.parent_layout2);
            this.card = itemView.findViewById(R.id.materialCardView);
        }
    }

    public class CallCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView desc;
        ConstraintLayout parentLayout;
        MaterialCardView card;

        CallCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.desc = itemView.findViewById(R.id.textView7);
            this.parentLayout = itemView.findViewById(R.id.parent_layout);
            this.card = itemView.findViewById(R.id.materialCardView);
        }
    }
}
