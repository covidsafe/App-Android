package edu.uw.covidsafe.contact_trace;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.symptoms.AddEditSymptomsFragment;
import edu.uw.covidsafe.symptoms.SymptomsOpsAsyncTask;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.notif.NotifRecord;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

public class HumanSummaryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Activity av;
    View view;
    List<HumanRecord> records = new LinkedList<>();

    public HumanSummaryRecyclerViewAdapter(Context mContext, Activity av, View view) {
        this.mContext = mContext;
        this.av = av;
        this.view = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_human, parent, false);
        return new HumanSummaryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HumanRecord record = this.records.get(position);

        if (record.getImgUri() != null) {
            Uri uri = Uri.parse(record.getImgUri());
            ((HumanSummaryHolder) holder).imageView11.setImageURI(uri);
        }

        String out = record.getName();
        if (record.getPhoneNumber() != null) {
            out += "\n"+record.getPhoneNumber();
        }
        ((HumanSummaryHolder) holder).text.setText(out);
        ((HumanSummaryHolder) holder).bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeMenu(((HumanSummaryHolder) holder).bb, record);
            }
        });
    }

    public void setRecords(List<HumanRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.records.size();
    }

    public class HumanSummaryHolder extends RecyclerView.ViewHolder {
        ImageView imageView11;
        TextView text;
        ImageButton bb;

        HumanSummaryHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView11 = itemView.findViewById(R.id.imageView11);
            this.text = itemView.findViewById(R.id.text);
            this.bb = itemView.findViewById(R.id.overflow);
        }
    }

    public void makeMenu(View view, HumanRecord record) {
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
//        ((MenuBuilder)popup.getMenu()).setOptionalIconsVisible(true);
        inflater.inflate(R.menu.overflow_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.editItem).setVisible(false);

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteItem:
                        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                                .setTitle(mContext.getString(R.string.sure_delete))
                                .setNegativeButton(mContext.getString(R.string.cancel), null)
                                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SimpleDateFormat outformat = new SimpleDateFormat("MM/dd h:mm aa");
                                        new HumanOpsAsyncTask(mContext, Constants.HumanDatabaseOps.Delete, record).execute();
                                    }
                                })
                                .setCancelable(true).create();
                        dialog.show();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}
