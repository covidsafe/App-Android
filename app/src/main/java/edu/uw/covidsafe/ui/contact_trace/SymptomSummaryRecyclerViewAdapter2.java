package edu.uw.covidsafe.ui.contact_trace;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.ui.symptoms.SymptomsRecord;

public class SymptomSummaryRecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    View view;
    List<SymptomsRecord> records = new LinkedList<>();
    int count = 0;

    public SymptomSummaryRecyclerViewAdapter2(Context mContext, View view) {
        this.mContext = mContext;
        this.view = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_summary_card, parent, false);
        return new SymptomSummaryHolder(view);
    }

    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    DecimalFormat df = new DecimalFormat("#.##");

    int currentSubSymptomCounter = 0;
    int currentSymptomCounter = 0;

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            currentSubSymptomCounter = 0;
            currentSymptomCounter = 0;
        }

        SymptomsRecord record = records.get(currentSymptomCounter);
        String symptom = "";
        if (currentSubSymptomCounter < record.getSymptoms(mContext).size()) {
            symptom = record.getSymptoms(mContext).get(currentSubSymptomCounter);
        }

        Log.e("adapter","currentSymptomCounter "+currentSymptomCounter);
        Log.e("adapter","currentSubSymptomCounter "+currentSubSymptomCounter);
        Log.e("adapter","size "+record.getSymptoms(mContext).size());

        currentSubSymptomCounter++;
        if (currentSubSymptomCounter >= record.getSymptoms(mContext).size()) {
            currentSymptomCounter++;
            currentSubSymptomCounter = 0;
        }

        ((SymptomSummaryHolder) holder).details.setText("");
        ((SymptomSummaryHolder) holder).details.setVisibility(View.GONE);

        if (symptom.equals(mContext.getString(R.string.fever_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.fever_txt);
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
        else if (symptom.equals(mContext.getString(R.string.abdominal_pain_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.abdominal_pain_txt);
        }
        else if (symptom.equals(mContext.getString(R.string.chills_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.chills_txt);
        }
        else if (symptom.equals(mContext.getString(R.string.cough_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.cough_text);
            String details = "";
            if (record.getCoughOnset() != 0) {
                details +=  mContext.getString(R.string.onset_date)+": "+format.format(record.getCoughOnset())+"\n";
            }
            if (record.getCoughDaysExperienced() != 0) {
                details +=  mContext.getString(R.string.duration)+": "+record.getCoughDaysExperienced()+"\n";
            }
            if (!record.getCoughSeverity().isEmpty()) {
                details += mContext.getString(R.string.severity)+": "+record.getCoughSeverity()+"\n";
            }
            ((SymptomSummaryHolder) holder).details.setText(details);
            if (!details.trim().isEmpty()) {
                ((SymptomSummaryHolder) holder).details.setVisibility(View.VISIBLE);
            }
        }
        else if (symptom.equals(mContext.getString(R.string.diarrhea_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.diarrhea_txt);
        }
        else if (symptom.equals(mContext.getString(R.string.trouble_breathing_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.difficult_in_breathing);
        }
        else if (symptom.equals(mContext.getString(R.string.headache_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.headache_txt);
        }
        else if (symptom.equals(mContext.getString(R.string.chest_pain_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.chest_pain_txt);
        }
        else if (symptom.equals(mContext.getString(R.string.sore_throat_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.sore_throat_txt);
        }
        else if (symptom.equals(mContext.getString(R.string.vomiting_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.vomiting_txt);
        }
        else if (symptom.equals(mContext.getString(R.string.no_symptoms_txt))) {
            ((SymptomSummaryHolder) holder).symptom.setText(R.string.no_symptoms_txt);
        }
        else {
            ((SymptomSummaryHolder) holder).layout.setVisibility(View.GONE);
        }
    }

    public void setRecords(List<SymptomsRecord> records) {
        this.records = records;
        for (SymptomsRecord record : records) {
            count += record.getSymptoms(mContext).size();
        }
    }

    @Override
    public int getItemCount() {
        return this.count;
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
