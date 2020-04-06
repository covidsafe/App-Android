package edu.uw.covidsafe.ui.settings;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class PermissionsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();
    Context cxt;
    Activity av;

    public PermissionsRecyclerViewAdapter(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
        titles.add("Notifications");
        titles.add("Location Sharing");
        titles.add("Bluetooth Tracing");
        desc.add(cxt.getString(R.string.perm1desc));
        desc.add(cxt.getString(R.string.perm2desc));
        desc.add(cxt.getString(R.string.perm3desc));
        icons.add(cxt.getDrawable(R.drawable.perm1));
        icons.add(cxt.getDrawable(R.drawable.perm2));
        icons.add(cxt.getDrawable(R.drawable.perm3));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_permission, parent, false);
        return new PermissionCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PermissionCard)holder).title.setText(titles.get(position));
        ((PermissionCard)holder).desc.setText(desc.get(position));
        ((PermissionCard)holder).icon.setImageDrawable(icons.get(position));
        ((PermissionCard)holder).sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (titles.get(position).toLowerCase().contains("notification")) {

                }
                else if (titles.get(position).toLowerCase().contains("location")) {

                    if (Constants.GPS_ENABLED && !Utils.hasGpsPermissions(av)) {
//                            if ((Constants.GPS_ENABLED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !Utils.hasGpsPermissions(getActivity())) {
                        Log.e("aa","PERMS");
                        ActivityCompat.requestPermissions(av, Constants.gpsPermissions, 2);
                    }

//                        if ((Constants.GPS_ENABLED || Constants.BLUETOOTH_ENABLED) && Utils.permCheck(getActivity())) {
                    if ((Constants.GPS_ENABLED && Utils.gpsCheck(av)) ||
                            (Constants.BLUETOOTH_ENABLED && Utils.bleCheck(av))) {
                        Utils.startBackgroundService(av);
                    }
                }
                else if (titles.get(position).toLowerCase().contains("bluetooth")) {

                    BluetoothManager bluetoothManager =
                            (BluetoothManager) av.getSystemService(Context.BLUETOOTH_SERVICE);
                    Constants.blueAdapter = bluetoothManager.getAdapter();

                    if (Constants.BLUETOOTH_ENABLED && !Utils.hasBlePermissions(av)) {
                        Log.e("aa","NO BLE PERMS");
                        ActivityCompat.requestPermissions(av, Constants.blePermissions, 1);
                    }

                    if (Utils.hasBlePermissions(av) &&
                            Constants.BLUETOOTH_ENABLED && (Constants.blueAdapter == null || !Constants.blueAdapter.isEnabled())) {
                        Log.e("aa","BLE");
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        av.startActivityForResult(enableBtIntent, 0);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class PermissionCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView desc;
        Switch sw;

        PermissionCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.title = itemView.findViewById(R.id.perm1);
            this.desc = itemView.findViewById(R.id.perm1desc);
            this.sw = itemView.findViewById(R.id.permSwitch);
        }
    }
}

