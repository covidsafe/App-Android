package edu.uw.covidsafe.ui.health;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

public class TipRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Object> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();

    private Context mContext;
    private Activity av;

    public TipRecyclerViewAdapter(Context mContext, Activity av) {
        this.mContext = mContext;
        this.av = av;
    }

    public void enableTips(int size, View view) {
//        Log.e("tip","enabletips");
        TextView tv = view.findViewById(R.id.sick);
        if (size>=1 && titles.size()==0) {

            titles.add("");
            desc.add("Call 911 immediately if you are having a medical emergency.");
            icons.add(mContext.getDrawable(R.drawable.icon_phone3));

            titles.add("Self-quarantine for 14 days");
            desc.add(getQuarantineTime());
            icons.add(mContext.getDrawable(R.drawable.icon_quarantine));

            titles.add("Monitor Your Symptoms");
            desc.add("If you think you have been exposed to COVID-19 and develop a fever and symptoms, such as cough or difficulty breathing, call your healthcare provider for medical advice.");
            icons.add(mContext.getDrawable(R.drawable.icon_symptoms));

            titles.add("Request a test");
            desc.add("If you have symptoms of COVID-19 and want to get tested, try calling your state or local health department or a medical provider.");
            icons.add(mContext.getDrawable(R.drawable.icon_test));
//
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
                        tv.setText("What to Do If You Are Sick");
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
//        Log.e("tip","oncreateviewholder "+position);
        if (titles.get(position).isEmpty()) {
            ((CallCard) holder).desc.setText((String)desc.get(position));
            ((CallCard)holder).icon.setImageDrawable(icons.get(position));
            ((CallCard)holder).card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constants.PUBLIC_DEMO) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                                .setMessage("This function is disabled in the demo version of the app.")
                                .setPositiveButton("Ok",null)
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
            if (titles.get(position).equals("Self-quarantine for 14 days")) {
                ((ActionCard) holder).desc.setText((Spannable) desc.get(position));
            }
            else {
                ((ActionCard) holder).desc.setText((String) desc.get(position));
            }
            ((ActionCard)holder).icon.setImageDrawable(icons.get(position));
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
        ConstraintLayout parentLayout;
        MaterialCardView card;

        ActionCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.title = itemView.findViewById(R.id.textView7);
            this.title.setMovementMethod(LinkMovementMethod.getInstance());
            this.desc = itemView.findViewById(R.id.textView5);
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
