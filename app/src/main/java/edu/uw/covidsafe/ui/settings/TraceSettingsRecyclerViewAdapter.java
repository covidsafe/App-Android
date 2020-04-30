package edu.uw.covidsafe.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.utils.Constants;

public class TraceSettingsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context cxt;
    Activity av;
    View view;
    List<String> names = new LinkedList<>();
    List<String> descs = new LinkedList<>();
    List<Drawable> icons = new LinkedList<>();
    OnHeadlineSelectedListener callback;

    public TraceSettingsRecyclerViewAdapter(Context cxt, Activity av, View view) {
        this.cxt = cxt;
        this.av = av;
        this.view = view;
        this.names.add(cxt.getString(R.string.setting1));
        this.names.add(cxt.getString(R.string.language));
        this.descs.add(cxt.getString(R.string.setting1desc));
//        this.descs.add(cxt.getString(R.string.english) + "/" + cxt.getString(R.string.spanish));
        this.descs.add("");
        this.icons.add(cxt.getDrawable(R.drawable.datalength));
        this.icons.add(cxt.getDrawable(R.drawable.lang));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_spinner_setting, parent, false);
        return new TracesettingsCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TracesettingsCard)holder).name.setText(this.names.get(position));
        ((TracesettingsCard)holder).desc.setText(this.descs.get(position));
        ((TracesettingsCard)holder).icon.setImageDrawable(this.icons.get(position));

        SharedPreferences prefs = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);

        if (position == 0) {

            int currentDaysOfDataToKeep = 0;
            if (Constants.DEBUG) {
                currentDaysOfDataToKeep = prefs.getInt(cxt.getString(R.string.infection_window_in_days_pkeys), Constants.DefaultDaysOfLogsToKeepDebug);
            }
            else {
                currentDaysOfDataToKeep = prefs.getInt(cxt.getString(R.string.infection_window_in_days_pkeys), Constants.DefaultDaysOfLogsToKeep);
            }

            int selectedIndex = 0;
            int j = 0;
            List<String> ii = new LinkedList<>();
            for (int i = currentDaysOfDataToKeep; i >= 1; i--) {
                ii.add(i + "");
                if (i == currentDaysOfDataToKeep) {
                    selectedIndex = j;
                }
                j++;
            }
            ((TracesettingsCard) holder).spinner.setItems(ii);
            ((TracesettingsCard) holder).spinner.setSelectedIndex(selectedIndex);

            ((TracesettingsCard) holder).spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    Log.e("setting ", "got " + item);
                    SharedPreferences.Editor editor = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                    editor.putInt(cxt.getString(R.string.infection_window_in_days_pkeys), Integer.parseInt(item));
                    editor.commit();
                }
            });
        }

        if (position == 1) {
            ((TracesettingsCard) holder).spinner.setItems(Constants.languages);
            ((TracesettingsCard) holder).spinner.setSelectedIndex(
                    Constants.languages.indexOf(prefs.getString(cxt.getString(R.string.lang_pkey), "en")));

            ((TracesettingsCard) holder).spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    Log.e("setting ", "got " + item);
                    SharedPreferences.Editor editor = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                    editor.putString(cxt.getString(R.string.lang_pkey), Constants.languages.get(position));
                    editor.commit();

                    callback.onRefreshSelected();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.names.size();
    }

    public class TracesettingsCard extends RecyclerView.ViewHolder {

        TextView name;
        TextView desc;
        MaterialSpinner spinner;
        ImageView icon;

        TracesettingsCard(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.settingName);
            this.desc = itemView.findViewById(R.id.settingDesc);
            this.spinner = itemView.findViewById(R.id.spinner);
            this.icon = itemView.findViewById(R.id.icon);
        }
    }

    public void setOnHeadlineSelectedListener(OnHeadlineSelectedListener callback) {
        this.callback = callback;
    }

    public interface OnHeadlineSelectedListener {
        void onRefreshSelected();
    }
}

