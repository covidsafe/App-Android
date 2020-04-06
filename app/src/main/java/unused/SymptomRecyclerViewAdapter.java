package unused;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.symptoms.SymptomsOpsAsyncTask;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class SymptomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> symptoms = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<CheckBox> boxes = new ArrayList<>();
    private Context mContext;
    private Activity av;

    public SymptomRecyclerViewAdapter(Context mContext, Activity av) {
        this.mContext = mContext;
        this.av = av;
        symptoms.add("Fever");
        symptoms.add("Cough");
        symptoms.add("Shortness of breath");
        symptoms.add("Trouble breathing");
        symptoms.add("Persistent pain or pressure in the chest");
        symptoms.add("New confusion or inability to arouse");
        symptoms.add("Bluish lips or face");
        symptoms.add("");
        desc.add("A new, continuous cough - this means you've started coughing repeatedly");
        desc.add("A high temperature of over 100°F - you feel hot to touch on your chest or back");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_list_layout, parent, false);
            return new SymptomHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_buttons, parent, false);
            return new ButtonHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == desc.size()-1) {
            ((ButtonHolder)holder).clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(CheckBox box : boxes) {
                        box.setChecked(false);
                    }
                    ((ButtonHolder)holder).certBox.setChecked(false);
                }
            });

            ((ButtonHolder)holder).submitButton.setEnabled(Utils.canSubmitSymptoms(mContext, Constants.SubmitThresh));

            ((ButtonHolder)holder).lastSubmitted.setText(Utils.getLastSymptomReportDate(mContext));

            ((ButtonHolder)holder).submitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!((ButtonHolder)holder).certBox.isChecked()) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                                .setTitle("Error")
                                .setMessage(av.getString(R.string.certError))
                                .setPositiveButton(R.string.ok,null)
                                .setCancelable(false).create();
                        dialog.show();
                    }
                    else if (!anyChecked()) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                                .setTitle("Error")
                                .setMessage(av.getString(R.string.noneCheckedError))
                                .setPositiveButton(R.string.ok,null)
                                .setCancelable(false).create();
                        dialog.show();
                    }
                    else {
                        ((ButtonHolder)holder).submitButton.setEnabled(false);
                        submitSymptomForm();
                    }
                }
            });
        }
        else {
            ((SymptomHolder)holder).imageName.setText(symptoms.get(position));
            ((SymptomHolder)holder).desc.setText(desc.get(position));
            boxes.add(((SymptomHolder)holder).cb);
            ((SymptomHolder)holder).parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boxes.get(position).setChecked(!boxes.get(position).isChecked());
                }
            });
        }
    }

    public boolean anyChecked() {
        for(CheckBox box : boxes) {
            if (box.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public void submitSymptomForm() {
        Utils.updateSymptomSubmitTime(av);
        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                .setTitle("Thank you")
                .setPositiveButton(R.string.ok,null)
                .setCancelable(false).create();
        dialog.show();
        SymptomsRecord rec = new SymptomsRecord(System.currentTimeMillis(),
                boxes.get(0).isChecked(),
                boxes.get(1).isChecked(),
                boxes.get(2).isChecked(),
                boxes.get(3).isChecked(),
                boxes.get(4).isChecked(),
                boxes.get(5).isChecked(),
                boxes.get(6).isChecked());
        new SymptomsOpsAsyncTask(mContext, rec).execute();
//        NetworkHelper.sendRecords(rec.toJson(), mContext);
    }

    @Override
    public int getItemViewType(int position) {
        if (!desc.get(position).isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

    public class SymptomHolder extends RecyclerView.ViewHolder {
        TextView imageName;
        TextView desc;
        ConstraintLayout parentLayout;
        CheckBox cb;

        SymptomHolder(@NonNull View itemView) {
            super(itemView);
            this.imageName =itemView.findViewById(R.id.symptom_name);
            this.desc =itemView.findViewById(R.id.symptom_desc);
            this.parentLayout = itemView.findViewById(R.id.parent_layout);
            this.cb = itemView.findViewById(R.id.symptom_checkbox);
        }
    }

    public class ButtonHolder extends RecyclerView.ViewHolder {
        Button submitButton;
        Button clearButton;
        TextView lastSubmitted;
        CheckBox certBox;
        ConstraintLayout parentLayout;

        ButtonHolder(@NonNull View itemView) {
            super(itemView);
            this.submitButton = itemView.findViewById(R.id.submitForm);
            this.clearButton = itemView.findViewById(R.id.submitClear);
            this.lastSubmitted = itemView.findViewById(R.id.lastSubmittedDate);
            this.certBox = itemView.findViewById(R.id.certBoxReport);
            this.parentLayout = itemView.findViewById(R.id.parent_layout2);
        }
    }
}
