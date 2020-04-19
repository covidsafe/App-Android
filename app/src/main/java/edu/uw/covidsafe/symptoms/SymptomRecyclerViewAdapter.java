package edu.uw.covidsafe.symptoms;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.symptoms.SymptomsOpsAsyncTask;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

public class SymptomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> symptoms = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    List<Boolean> statesIn = new ArrayList<>();
    List<Boolean> statesOut = new ArrayList<>();
    private Context mContext;
    private Activity av;
    private String op;

    public SymptomRecyclerViewAdapter(Context mContext, Activity av, String op) {
        this.mContext = mContext;
        this.av = av;
        this.op = op;

        symptoms.add("Fever");
        symptoms.add("Abdominal pain");
        symptoms.add("Chills");
        symptoms.add("Cough");
        symptoms.add("Diarrhea");
        symptoms.add("Difficulty breathing");
        symptoms.add("Headache");
//        symptoms.add("Muscles aches/pains");
//        symptoms.add("Sore throat");
//        symptoms.add("Vomitting");

//        desc.add("A high temperature of over 100°F - you feel hot to touch on your chest or back");
//        desc.add("A new, continuous cough - this means you've started coughing repeatedly");
//        desc.add("Shortness of breath, or dyspnea, is an uncomfortable condition that makes it difficult to fully get air into your lungs.");
//        desc.add("Chest pain appears in many forms, ranging from a sharp stab to a dull ache. Sometimes chest pain feels crushing or burning. In certain cases, the pain travels up the neck, into the jaw, and then radiates to the back or down one or both arms.");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
//        desc.add("tbd");
//        desc.add("tbd");
//        desc.add("tbd");

        if (statesIn.size() > 0) {
            for (Boolean b : statesIn) {
                statesOut.add(b);
            }
        }
        else {
            for (int i = 0; i < symptoms.size(); i++) {
                statesOut.add(false);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_list_layout, parent, false);
        return new SymptomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SymptomHolder)holder).name.setText(symptoms.get(position));
        ((SymptomHolder)holder).desc.setText(desc.get(position));

        if (this.op.equals("edit") && statesIn != null) {
            ((SymptomHolder) holder).cb.setChecked(statesIn.get(position));
            if (position == statesIn.size()) {
                statesIn = null;
            }
        }

        ((SymptomHolder) holder).cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                statesOut.set(position, isChecked);
            }
        });

        ((SymptomHolder) holder).details.setVisibility(View.GONE);
        ((SymptomHolder) holder).details2.setVisibility(View.GONE);

        String symptomName = ((SymptomHolder)holder).name.getText().toString().toLowerCase();
        if (symptomName.contains("fever") || symptomName.contains("cough")) {
            ((SymptomHolder)holder).chevron.setVisibility(View.VISIBLE);
            ((SymptomHolder)holder).innerConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean state = !((SymptomHolder)holder).detailsVisible;
                    ((SymptomHolder)holder).detailsVisible = state;

                    if (state) {
                        ((SymptomHolder) holder).chevron.setImageDrawable(mContext.getDrawable(R.drawable.ic_keyboard_arrow_down_gray_24dp));
                        if (symptomName.contains("fever")) {
                            ((SymptomHolder) holder).details.setVisibility(View.VISIBLE);
                        }
                        else if (symptomName.contains("cough")){
                            ((SymptomHolder) holder).details2.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        ((SymptomHolder) holder).chevron.setImageDrawable(mContext.getDrawable(R.drawable.ic_navigate_before_black_24dp));
                        if (symptomName.contains("fever")) {
                            ((SymptomHolder) holder).details.setVisibility(View.GONE);
                        }
                        else if (symptomName.contains("cough")){
                            ((SymptomHolder) holder).details2.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
        else {
            ((SymptomHolder)holder).details.setVisibility(View.GONE);
            ((SymptomHolder)holder).details2.setVisibility(View.GONE);
            ((SymptomHolder)holder).chevron.setVisibility(View.GONE);
            ((SymptomHolder)holder).innerConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SymptomHolder) holder).cb.setChecked(!((SymptomHolder) holder).cb.isChecked());
                }
            });
        }

//        Log.e("symptoms","on bind "+position+","+((SymptomHolder)holder).detailsVisible);
//        ImageView chevron = ((SymptomHolder)holder).chevron;
//        if (((SymptomHolder)holder).detailsVisible) {
//            ((SymptomHolder) holder).details.setVisibility(View.VISIBLE);
//            chevron.setImageDrawable(mContext.getDrawable(R.drawable.ic_keyboard_arrow_down_gray_24dp));
//        }
//        else {
//            ((SymptomHolder) holder).details.setVisibility(View.GONE);
//            chevron.setImageDrawable(mContext.getDrawable(R.drawable.ic_navigate_before_black_24dp));
//        }
    }

    public void updateContent(List<Boolean> states) {
        this.statesIn = states;
        statesOut = new LinkedList<>();
        for (Boolean b : statesIn) {
            statesOut.add(b);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

    public class SymptomHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView desc;
        ConstraintLayout parentLayout;
        ConstraintLayout innerConstraintLayout;
        CheckBox cb;
        ImageView chevron;
        boolean detailsVisible = false;
        ConstraintLayout details;
        ConstraintLayout details2;

        SymptomHolder(@NonNull View itemView) {
            super(itemView);
            this.name =itemView.findViewById(R.id.symptom_name);
            this.desc =itemView.findViewById(R.id.symptom_desc);
            this.parentLayout = itemView.findViewById(R.id.parent_layout);
            this.innerConstraintLayout = itemView.findViewById(R.id.innerConstraintLayout);
            this.details = itemView.findViewById(R.id.details);
            this.details2 = itemView.findViewById(R.id.details2);
            this.chevron = itemView.findViewById(R.id.chevron);
            this.cb = itemView.findViewById(R.id.symptom_checkbox);

//            this.innerConstraintLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("symptoms","item changed at "+getAdapterPosition()+","+(!detailsVisible));
//                    detailsVisible = !detailsVisible;
//                    notifyItemChanged(getAdapterPosition());
//                }
//            });
        }
    }
}
