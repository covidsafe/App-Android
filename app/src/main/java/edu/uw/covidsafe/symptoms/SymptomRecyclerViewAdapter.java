package edu.uw.covidsafe.symptoms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.covidsafe.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.jaredrummler.materialspinner.MaterialSpinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.symptoms.SymptomsOpsAsyncTask;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.settings.TraceSettingsRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

public class SymptomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public SymptomsRecord dataIn = new SymptomsRecord();
    public SymptomsRecord dataOut = new SymptomsRecord();
    private Context mContext;
    private Activity av;
    private String op;
    List<String> feverSpinner;
    List<String> coughSeverity;
    DatePickerDialog dialog;
    Calendar myCalendar = Calendar.getInstance();

    public SymptomRecyclerViewAdapter(Context mContext, Activity av, String op) {
        this.mContext = mContext;
        this.av = av;
        this.op = op;

        feverSpinner = new LinkedList<>();
        feverSpinner.add("°F");
        feverSpinner.add("°C");

        coughSeverity = new LinkedList<>();
        coughSeverity.add("Mild");
        coughSeverity.add("Moderate");
        coughSeverity.add("Severe");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_list_layout, parent, false);
        return new SymptomHolder(view);
    }

    // if cb is null
    // update the object with the contents of the form (to get result back to the parent)

    // if cb is not null
    // update the checkbox with the contents of the object (when restoring results for edit op)
    public void updateCheckbox(int position, CheckBox cb, boolean isChecked) {
        String symptom = Constants.symptoms.get(position).toLowerCase();
        if (symptom.equals("fever")) {
            if (cb != null) { cb.setChecked(dataIn.isFever()); }
            else { dataOut.setFever(isChecked); }
        }
        else if (symptom.contains("abdominal pain")) {
            if (cb != null) { cb.setChecked(dataIn.isAbdominalPain()); }
            else { dataOut.setAbdominalPain(isChecked); }
        }
        else if (symptom.contains("chills")) {
            if (cb != null) { cb.setChecked(dataIn.isChills()); }
            else { dataOut.setChills(isChecked); }
        }
        else if (symptom.contains("cough")) {
            if (cb != null) { cb.setChecked(dataIn.isCough()); }
            else { dataOut.setCough(isChecked); }
        }
        else if (symptom.contains("diarrhea")) {
            if (cb != null) { cb.setChecked(dataIn.isDiarrhea()); }
            else { dataOut.setDiarrhea(isChecked); }
        }
        else if (symptom.contains("difficult breathing")) {
            if (cb != null) { cb.setChecked(dataIn.isTroubleBreathing()); }
            else { dataOut.setTroubleBreathing(isChecked); }
        }
        else if (symptom.contains("headache")) {
            if (cb != null) { cb.setChecked(dataIn.isHeadache()); }
            else { dataOut.setHeadache(isChecked); }
        }
        else if (symptom.contains("sore throat")) {
            if (cb != null) { cb.setChecked(dataIn.isSoreThroat()); }
            else { dataOut.setSoreThroat(isChecked); }
        }
        else if (symptom.contains("vomiting")) {
            if (cb != null) { cb.setChecked(dataIn.isVomiting()); }
            else { dataOut.setVomiting(isChecked); }
        }
    }

    public void initFeverForm(RecyclerView.ViewHolder holder) {
        dataOut.setFeverUnit(this.feverSpinner.get(0));
        ((SymptomHolder) holder).feverSpinner.setItems(this.feverSpinner);
        ((SymptomHolder) holder).feverSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                dataOut.setFeverUnit(feverSpinner.get(position));
            }
        });
        ((SymptomHolder) holder).feverDaysExperienced.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int num = 0;
                try {
                    num = Integer.parseInt(((SymptomHolder) holder).feverDaysExperienced.getText().toString());
                    ((SymptomHolder) holder).cb.setChecked(true);
                }
                catch(Exception e){
                    Log.e("err",e.getMessage());
                }
                dataOut.setFeverDaysExperienced(num);
                checkFeverEmpty(((SymptomHolder) holder));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        ((SymptomHolder) holder).feverTemp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double num = 0;
                try {
                    num = Double.parseDouble(((SymptomHolder) holder).feverTemp.getText().toString());
                    ((SymptomHolder) holder).cb.setChecked(true);
                }
                catch(Exception e) {
                    Log.e("err",e.getMessage());
                }
                dataOut.setFeverTemp(num);
                checkFeverEmpty(((SymptomHolder) holder));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        ((SymptomHolder) holder).feverOnsetDate.setInputType(InputType.TYPE_NULL);
        ((SymptomHolder) holder).feverOnsetDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                calendarSetup(((SymptomHolder) holder), ((SymptomHolder) holder).feverOnsetDate, "fever");
                return false;
            }
        });
    }

    public void checkFeverEmpty(SymptomHolder holder) {
        String onsetDate = holder.feverOnsetDate.getText().toString().trim();
        String temp = holder.feverTemp.getText().toString().trim();
        String daysExperienced = holder.feverDaysExperienced.getText().toString().trim();
        if (onsetDate.isEmpty() && temp.isEmpty() && (daysExperienced.isEmpty() || Integer.parseInt(daysExperienced) <= 0)) {
            holder.cb.setChecked(false);
        }
        else {
            holder.cb.setChecked(true);
        }
    }

    public void checkCoughEmpty(SymptomHolder holder) {
        String onsetDate = holder.coughOnsetDate.getText().toString().trim();
        String daysExperienced = holder.coughDaysExperienced.getText().toString().trim();
        boolean c1 = holder.chip1.isChecked();
        boolean c2 = holder.chip2.isChecked();
        boolean c3 = holder.chip3.isChecked();
        boolean anyChecked = c1||c2||c3;
        if (onsetDate.isEmpty() && (daysExperienced.isEmpty() || Integer.parseInt(daysExperienced) <= 0) && !anyChecked) {
            holder.cb.setChecked(false);
        }
        else {
            holder.cb.setChecked(true);
        }
    }

    private void updateLabel(TextInputEditText tt, String symptom) {
        String myFormat = "MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String date = sdf.format(myCalendar.getTime());
        tt.setText(date);
        if (symptom.equals("cough")) {
            dataOut.setCoughOnset(myCalendar.getTime().getTime());
        }
        else if (symptom.equals("fever")) {
            dataOut.setFeverOnset(myCalendar.getTime().getTime());
        }
    }

    public void initCoughForm(RecyclerView.ViewHolder holder) {
        if (dataIn.getCoughDaysExperienced() > 0) {
            ((SymptomHolder) holder).coughDaysExperienced.setText(dataIn.getCoughDaysExperienced() + "");
        }
        ((SymptomHolder) holder).coughDaysExperienced.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int num = 0;
                try {
                    num = Integer.parseInt(((SymptomHolder) holder).coughDaysExperienced.getText().toString());
                }
                catch(Exception e) {
                    Log.e("err",e.getMessage());
                }
                dataOut.setCoughDaysExperienced(num);
                checkCoughEmpty((SymptomHolder)holder);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        ((SymptomHolder) holder).chip1.setVisibility(View.GONE);
        ((SymptomHolder) holder).chip2.setVisibility(View.GONE);
        ((SymptomHolder) holder).chip3.setVisibility(View.GONE);
        ((SymptomHolder) holder).cg.setVisibility(View.GONE);

        ((SymptomHolder) holder).chip1.setChecked(false);
        ((SymptomHolder) holder).chip2.setChecked(false);
        ((SymptomHolder) holder).chip3.setChecked(false);
        ((SymptomHolder) holder).cg.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                if (i == ((SymptomHolder) holder).chip1.getId()) {
                    if (((SymptomHolder) holder).chip1.isChecked()) {
                        dataOut.setCoughSeverity(coughSeverity.get(0));
                    }
                    else {
                        dataOut.setCoughSeverity("");
                    }
                } else if (i == ((SymptomHolder) holder).chip2.getId()) {
                    if (((SymptomHolder) holder).chip2.isChecked()) {
                        dataOut.setCoughSeverity(coughSeverity.get(1));
                    }
                    else {
                        dataOut.setCoughSeverity("");
                    }
                } else if (i == ((SymptomHolder) holder).chip3.getId()) {
                    if (((SymptomHolder) holder).chip3.isChecked()) {
                        dataOut.setCoughSeverity(coughSeverity.get(2));
                    }
                    else {
                        dataOut.setCoughSeverity("");
                    }
                }
                checkCoughEmpty((SymptomHolder)holder);
            }
        });

        ((SymptomHolder) holder).coughOnsetDate.setInputType(InputType.TYPE_NULL);
        ((SymptomHolder) holder).coughOnsetDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                calendarSetup(((SymptomHolder) holder), ((SymptomHolder) holder).coughOnsetDate, "cough");
                return false;
            }
        });
    }

    public void symptomCheck(String symptom, SymptomHolder holder) {
        if(symptom.contains("fever")) {
            checkFeverEmpty(holder);
        }
        else if (symptom.contains("cough")){
            checkCoughEmpty(holder);
        }
    }

    public void calendarSetup(SymptomHolder holder, final TextInputEditText tt, String symptom) {
        if (dialog == null) {
            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    dialog = null;
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(tt, symptom);
                    symptomCheck(symptom, holder);
                }
            };

            dialog = new DatePickerDialog(mContext, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            SharedPreferences prefs = mContext.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            int days = prefs.getInt(mContext.getString(R.string.infection_window_in_days_pkeys), Constants.DefaultInfectionWindowInDays);

            dialog.getDatePicker().setMinDate(TimeUtils.getNDaysForward(-days));
            dialog.getDatePicker().setMaxDate(new Date(TimeUtils.getTime()).getTime());
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        dialog = null;
                    }
                    tt.setText("");
                    symptomCheck(symptom, holder);
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SymptomHolder)holder).name.setText(Constants.symptoms.get(position));
        ((SymptomHolder)holder).desc.setText(Constants.symptomDesc.get(position));

        String symptomName = Constants.symptoms.get(position).toLowerCase();
        ((SymptomHolder) holder).details.setVisibility(View.GONE);
        ((SymptomHolder) holder).details2.setVisibility(View.GONE);

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
                            initFeverForm(holder);
                        }
                        else if (symptomName.contains("cough")){
                            ((SymptomHolder) holder).details2.setVisibility(View.VISIBLE);
                            initCoughForm(holder);
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

        // restore states of checkboxes
        if (this.op.equals("edit") && dataIn != null) {
            updateCheckbox(position, ((SymptomHolder) holder).cb, false);
            if (symptomName.contains("fever") && ((SymptomHolder) holder).detailsVisible) {
                ////////////////////////////////////////////////////////////////////////
                int unitIndex = this.feverSpinner.indexOf(dataIn.getFeverUnit());
                ((SymptomHolder) holder).feverSpinner.setSelectedIndex(unitIndex);
                ////////////////////////////////////////////////////////////////////////
                SimpleDateFormat format = new SimpleDateFormat("MM/dd");
                String date = format.format(new Date(dataIn.getFeverOnset()));
                ((SymptomHolder) holder).feverOnsetDate.setText(date);
                ////////////////////////////////////////////////////////////////////////
                ((SymptomHolder) holder).feverDaysExperienced.setText(dataIn.getFeverDaysExperienced());
                ////////////////////////////////////////////////////////////////////////
                ((SymptomHolder) holder).feverTemp.setText(dataIn.getFeverTemp()+"");
                ////////////////////////////////////////////////////////////////////////
            }
            else if (symptomName.contains("cough") && ((SymptomHolder) holder).detailsVisible) {
                ////////////////////////////////////////////////////////////////////////
                SimpleDateFormat format = new SimpleDateFormat("MM/dd");
                String date = format.format(new Date(dataIn.getCoughOnset()));
                ((SymptomHolder) holder).coughOnsetDate.setText(date);
                ////////////////////////////////////////////////////////////////////////
                ((SymptomHolder) holder).chip1.setChecked(false);
                ((SymptomHolder) holder).chip2.setChecked(false);
                ((SymptomHolder) holder).chip3.setChecked(false);

                String coughSeverityStr = dataIn.getCoughSeverity();
                Log.e("symptom","asdf>>>"+coughSeverityStr);
                if (coughSeverityStr.toLowerCase().contains(this.coughSeverity.get(0))) {
                    ((SymptomHolder) holder).chip1.setChecked(true);
                }
                else if (coughSeverityStr.toLowerCase().contains(this.coughSeverity.get(1))) {
                    ((SymptomHolder) holder).chip2.setChecked(true);
                }
                else if (coughSeverityStr.toLowerCase().contains(this.coughSeverity.get(2))) {
                    ((SymptomHolder) holder).chip3.setChecked(true);
                }
                ////////////////////////////////////////////////////////////////////////
            }
            if (position == Constants.symptoms.size()) {
                dataIn = null;
            }
        }

        // restore states of objects
        ((SymptomHolder) holder).cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateCheckbox(position, null, isChecked);
            }
        });
    }

    public void updateContent(SymptomsRecord dataIn) {
        this.dataIn = dataIn;
        dataOut = dataIn.copy();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return Constants.symptoms.size();
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

        TextInputEditText feverOnsetDate;
        TextInputEditText feverTemp;
        TextInputEditText feverDaysExperienced;
        MaterialSpinner feverSpinner;

        TextInputEditText coughOnsetDate;
        TextInputEditText coughDaysExperienced;

        Chip chip1;
        Chip chip2;
        Chip chip3;
        ChipGroup cg;

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

            this.feverOnsetDate = itemView.findViewById(R.id.feverOnsetDate);
            this.feverTemp = itemView.findViewById(R.id.feverTemp);
            this.feverDaysExperienced = itemView.findViewById(R.id.feverDaysExperienced);
            this.feverSpinner = itemView.findViewById(R.id.spinner);

            this.coughOnsetDate = itemView.findViewById(R.id.coughOnsetDate);
            this.coughDaysExperienced = itemView.findViewById(R.id.coughDaysExperienced);

            this.chip1 = itemView.findViewById(R.id.chip1);
            this.chip2 = itemView.findViewById(R.id.chip2);
            this.chip3 = itemView.findViewById(R.id.chip3);
            this.cg = itemView.findViewById(R.id.chipGroup);
        }
    }
}
