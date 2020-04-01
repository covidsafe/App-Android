package edu.uw.covidsafe.ui.health;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidsafe.R;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private ArrayList<String> symptoms = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<CheckBox> boxes = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
        symptoms.add("Fever");
        symptoms.add("Cough");
        symptoms.add("Shortness of breath");
        symptoms.add("Trouble breathing");
        symptoms.add("Persistent pain or pressure in the chest");
        symptoms.add("New confusion or inability to arouse");
        symptoms.add("Bluish lips or face");
        desc.add("A new, continuous cough - this means you've started coughing repeatedly");
        desc.add("A high temperature of over 100°F - you feel hot to touch on your chest or back");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
        desc.add("tbd");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_list_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.imageName.setText(symptoms.get(position));
        holder.desc.setText(desc.get(position));
        boxes.add(holder.cb);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boxes.get(position).setChecked(!boxes.get(position).isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView imageName;
        TextView desc;
        ConstraintLayout parentLayout;
        CheckBox cb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageName =itemView.findViewById(R.id.symptom_name);
            this.desc =itemView.findViewById(R.id.symptom_desc);
            this.parentLayout = itemView.findViewById(R.id.parent_layout);
            this.cb = itemView.findViewById(R.id.symptom_checkbox);
        }
    }
}
