package edu.uw.covidsafe.gps;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.symptoms.SymptomsOpsAsyncTask;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.notif.NotifRecord;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

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
        View view ;
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
                    Log.e("err",e.getMessage());
                    ((GpsHistoryHolder)holder).loc.setText("Unknown address");
                }
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("h:mm aa");
        String time = format.format(record.getTs());
        ((GpsHistoryHolder)holder).loc.setText(address);
        ((GpsHistoryHolder)holder).time.setText(time);
    }

    public void setRecords(List<GpsRecord> records, Context cxt) {
        Log.e("contact","set records");
        List<String> lats = new LinkedList<>();
        List<String> lons = new LinkedList<>();
        List<String> addresses = new LinkedList<>();
        for(GpsRecord record : records) {
            lats.add(record.getRawLat());
            lons.add(record.getRawLongi());
            addresses.add(record.getRawAddress());
        }

        String[] decryptedLats = CryptoUtils.decryptBatch(cxt, lats);
        String[] decryptedLons = CryptoUtils.decryptBatch(cxt, lons);
        String[] decryptedAddresses = CryptoUtils.decryptBatch(cxt, addresses);

        Set<String> seenAddresses = new HashSet<>();

        List<GpsRecord> filtRecords = new LinkedList<>();
        for(int i = 0; i <decryptedAddresses.length; i++) {
            if (!seenAddresses.contains(decryptedAddresses[i])) {
                GpsRecord newRec = new GpsRecord();
                newRec.setRawLat(decryptedLats[i]);
                newRec.setRawLongi(decryptedLons[i]);
                newRec.setRawAddress(decryptedAddresses[i]);
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

        GpsHistoryHolder(@NonNull View itemView) {
            super(itemView);
            this.loc = itemView.findViewById(R.id.loc);
            this.time = itemView.findViewById(R.id.time);
        }
    }
}
