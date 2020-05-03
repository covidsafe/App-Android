package edu.uw.covidsafe.gps;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;

public class GpsHistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Activity av;
    View view;
    List<GpsRecord> records = new LinkedList<>();

    public GpsHistoryRecyclerViewAdapter(Context mContext, Activity av, View view) {
        this.mContext = mContext;
        this.av = av;
        this.view = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gps_history_log, parent, false);
        return new GpsHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GpsRecord record = this.records.get(position);
        String address = record.getRawAddress();
        if (address == null || address.isEmpty()) {
            Geocoder gc = new Geocoder(mContext);
            if (gc.isPresent()) {
                try {
                    List<Address> addresses = gc.getFromLocation(record.getLat(mContext),
                            record.getLongi(mContext), 1);
                    address = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    Log.e("err", e.getMessage());
                    ((GpsHistoryHolder) holder).loc.setText(R.string.unknown_address_txt);
                }
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("h:mm aa");
        String time = format.format(record.getTs_start());
        ((GpsHistoryHolder) holder).loc.setText(address);
        ((GpsHistoryHolder) holder).time.setText(time);
        ((GpsHistoryHolder) holder).bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(record);
//                makeMenu(((GpsHistoryHolder) holder).bb, record);
            }
        });
    }

    public void setRecords(List<GpsRecord> records, Context cxt) {
        Log.e("contact", "set records");
        List<String> lats = new LinkedList<>();
        List<String> lons = new LinkedList<>();
        List<String> addresses = new LinkedList<>();
        List<Long> ts = new LinkedList<>();
        for (GpsRecord record : records) {
            lats.add(record.getRawLat());
            lons.add(record.getRawLongi());
            addresses.add(record.getRawAddress());
            ts.add(record.getTs_start());
        }

        String[] decryptedLats = CryptoUtils.decryptBatch(cxt, lats);
        String[] decryptedLons = CryptoUtils.decryptBatch(cxt, lons);
        String[] decryptedAddresses = CryptoUtils.decryptBatch(cxt, addresses);

        Set<String> seenAddresses = new HashSet<>();

        List<GpsRecord> filtRecords = new LinkedList<>();
        for (int i = 0; i < decryptedAddresses.length; i++) {
            if (!seenAddresses.contains(decryptedAddresses[i])) {
                GpsRecord newRec = new GpsRecord();
                newRec.setRawLat(decryptedLats[i]);
                newRec.setRawLongi(decryptedLons[i]);
                newRec.setRawAddress(decryptedAddresses[i]);
                newRec.setTs_start(ts.get(i));
                filtRecords.add(newRec);
                seenAddresses.add(decryptedAddresses[i]);
            }
        }

        Collections.reverse(filtRecords);
        this.records = filtRecords;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.records.size();
    }

    public class GpsHistoryHolder extends RecyclerView.ViewHolder {
        TextView loc;
        TextView time;
        ImageView bb;

        GpsHistoryHolder(@NonNull View itemView) {
            super(itemView);
            this.loc = itemView.findViewById(R.id.loc);
            this.time = itemView.findViewById(R.id.time);
            this.bb = itemView.findViewById(R.id.overflow);
        }
    }

    public void makeMenu(View view, GpsRecord record) {
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

    public void showDialog(GpsRecord record) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                .setTitle(mContext.getString(R.string.sure_delete))
                .setNegativeButton(mContext.getString(R.string.cancel), null)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new GpsOpsAsyncTask(mContext, Constants.GpsDatabaseOps.Delete, record.getTs_start()).execute();
                    }
                })
                .setCancelable(true).create();
        dialog.show();
    }
}
