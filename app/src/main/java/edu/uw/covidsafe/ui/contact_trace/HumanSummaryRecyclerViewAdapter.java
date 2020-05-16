package edu.uw.covidsafe.ui.contact_trace;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.utils.Constants;

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
        if (record.getEmail() != null) {
            out += "\n"+record.getEmail();
        }
        ((HumanSummaryHolder) holder).text.setText(out);
        ((HumanSummaryHolder) holder).bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(record);
//                makeMenu(((HumanSummaryHolder) holder).bb, record);
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
        ImageView bb;

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
                        showDialog(record);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public void showDialog(HumanRecord record) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext)
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
    }
}
