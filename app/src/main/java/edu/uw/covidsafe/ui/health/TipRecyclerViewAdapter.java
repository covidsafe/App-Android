package edu.uw.covidsafe.ui.health;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.uw.covidsafe.utils.Constants;

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
        TextView tv = view.findViewById(R.id.sick);
        if (size>=1 && titles.size()==0) {
            titles.add("");
            titles.add("Self-quarantine for 14 days");
            titles.add("Monitor Your Symptoms");
            titles.add("Request a test");
            titles.add("Contact your healthcare professional");
            titles.add("Isolate from those around you");
            titles.add("Practice Good Hygiene");
            Date dd = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dd);
            calendar.add(Calendar.DATE, Constants.QuarantineLengthInDays);
            long thresh = calendar.getTime().getTime();

            SimpleDateFormat format = new SimpleDateFormat("MMMM d");
            String ss = format.format(new Date(thresh));

            desc.add("Call 911 immediately if you are having a medical emergency.");

            Spannable s = (Spannable) Html.fromHtml(
                    "If you start your self quarantine today, your 14 days will end <b>"+ss+"</b>. Please check with your local Health Authorities for more guidance."
            );

            desc.add(s);
            desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");
            desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");
            desc.add("Please contact your healthcare professional for next steps.");
            desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");
            desc.add("Egestas tellus rutrum tellus pellentesque eu tincidunt. Odio tempor orci dapibus ultrices in iaculis nunc sed augue suspendisse.");

            icons.add(mContext.getDrawable(R.drawable.icon_phone3));
            icons.add(mContext.getDrawable(R.drawable.icon_quarantine));
            icons.add(mContext.getDrawable(R.drawable.icon_symptoms));
            icons.add(mContext.getDrawable(R.drawable.icon_test));
            icons.add(mContext.getDrawable(R.drawable.icon_phone2));
            icons.add(mContext.getDrawable(R.drawable.icon_quarantine));
            icons.add(mContext.getDrawable(R.drawable.icon_health));
            av.runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("What to Do If You Are Sick");
                    tv.setVisibility(View.VISIBLE);
                }});
            Log.e("notif","tip changed");
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
            Log.e("notif","tip 0 changed");
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
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

        ActionCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.title = itemView.findViewById(R.id.textView7);
            this.title.setMovementMethod(LinkMovementMethod.getInstance());
            this.desc = itemView.findViewById(R.id.textView5);
            this.parentLayout = itemView.findViewById(R.id.parent_layout2);
        }
    }

    public class CallCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView desc;
        ConstraintLayout parentLayout;

        CallCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.desc = itemView.findViewById(R.id.textView7);
            this.parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
