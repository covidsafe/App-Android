package edu.uw.covidsafe.symptoms;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class SymptomSummaryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Activity av;
    View view;
    SymptomsRecord record = new SymptomsRecord();

    public SymptomSummaryRecyclerViewAdapter(Context mContext, Activity av, View view, SymptomsRecord record) {
        this.mContext = mContext;
        this.av = av;
        this.view = view;
        this.record = record;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_confirm_card, parent, false);
        return new SymptomSummaryHolder(view);
    }

    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String symptom = record.getSymptoms().get(position);

        ((SymptomSummaryHolder) holder).details.setText("");
        ((SymptomSummaryHolder) holder).details.setVisibility(View.GONE);

        if (symptom.equals("fever")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.fever_txt));
            String details = "";
            if (record.getFeverOnset() != 0) {
                details += mContext.getString(R.string.onset_date)+": "+format.format(record.getFeverOnset())+"\n";
            }
            if (record.getFeverTemp() != 0) {
                details += mContext.getString(R.string.highest_temperature_text)+": "+df.format(record.getFeverTemp())+record.getFeverUnit()+"\n";
            }
            if (record.getFeverDaysExperienced() != 0) {
                details += mContext.getString(R.string.duration)+": "+record.getFeverDaysExperienced()+"\n";
            }
            ((SymptomSummaryHolder) holder).details.setText(details);
            if (!details.trim().isEmpty()) {
                ((SymptomSummaryHolder) holder).details.setVisibility(View.VISIBLE);
            }
        }
        else if (symptom.equals("abdominal")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.abdominal_pain_txt));
        }
        else if (symptom.equals("chills")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.chills_txt));
        }
        else if (symptom.equals("cough")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.cough_txt));
            String details = "";
            if (record.getCoughOnset() != 0) {
                details += mContext.getString(R.string.onset_date)+": "+format.format(record.getCoughOnset())+"\n";
            }
            if (record.getCoughDaysExperienced() != 0) {
                details += mContext.getString(R.string.duration)+": "+record.getCoughDaysExperienced()+"\n";
            }
            if (!record.getCoughSeverity().isEmpty()) {
                details += mContext.getString(R.string.severity)+": "+record.getCoughSeverity()+"\n";
            }
            ((SymptomSummaryHolder) holder).details.setText(details);
            if (!details.trim().isEmpty()) {
                ((SymptomSummaryHolder) holder).details.setVisibility(View.VISIBLE);
            }
        }
        else if (symptom.equals("diarrhea")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.diarrhea_txt));
        }
        else if (symptom.equals("breathing")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.difficult_in_breathing));
        }
        else if (symptom.equals("headache")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.headache_txt));
        }
        else if (symptom.equals("chest")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.chest_pain_txt));
        }
        else if (symptom.equals("sore")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.sore_throat_txt));
        }
        else if (symptom.equals("vomiting")) {
            ((SymptomSummaryHolder) holder).symptom.setText(mContext.getResources().getString(R.string.vomitting_txt));
        }
        else {
            ((SymptomSummaryHolder) holder).layout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.record.numSymptoms();
    }

    public class SymptomSummaryHolder extends RecyclerView.ViewHolder {
        MaterialCardView layout;
        TextView symptom;
        TextView details;

        SymptomSummaryHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.symptomCard);
            this.symptom = itemView.findViewById(R.id.symptom);
            this.details = itemView.findViewById(R.id.details);
        }
    }
}
