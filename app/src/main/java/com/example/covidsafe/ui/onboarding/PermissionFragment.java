package com.example.covidsafe.ui.onboarding;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.covidsafe.R;
import com.example.covidsafe.ui.MainActivity;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;

public class PermissionFragment extends Fragment {

    Button finish;
    Button back;

    Switch notifSwitch;
    Switch gpsSwitch;
    Switch bleSwitch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_permissions, container, false);
        ((OnboardingActivity) getActivity()).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","permission fragment on resume");
        Constants.CurrentFragment = this;
        Constants.PermissionsFragment = this;

        finish = (Button) getActivity().findViewById(R.id.permissionsFinish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        back = (Button) getActivity().findViewById(R.id.permissionsBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding, new StartFragment()).commit();
            }
        });

        notifSwitch = (Switch) getActivity().findViewById(R.id.notifSwitch);
        gpsSwitch = (Switch) getActivity().findViewById(R.id.gpsSwitch);
        bleSwitch = (Switch) getActivity().findViewById(R.id.bleSwitch);

        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.notif_message), isChecked);
                editor.commit();
                Constants.NOTIFS_ENABLED = isChecked;
            }
        });
        bleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.ble_enabled_pkey), isChecked);
                editor.commit();
                Constants.BLUETOOTH_ENABLED = isChecked;

                if (Constants.BLUETOOTH_ENABLED && !Utils.hasBlePermissions(getActivity())) {
                    Log.e("aa","NO BLE PERMS");
                    ActivityCompat.requestPermissions(getActivity(), Constants.blePermissions, 1);
                }

                if (Utils.hasBlePermissions(getActivity()) &&
                    Constants.BLUETOOTH_ENABLED && (Constants.blueAdapter == null || !Constants.blueAdapter.isEnabled())) {
                    Log.e("aa","BLE");
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    getActivity().startActivityForResult(enableBtIntent, 0);
                }
            }
        });
        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.gps_enabled_pkey), isChecked);
                editor.commit();
                Constants.GPS_ENABLED = isChecked;
//                if ((Constants.GPS_ENABLED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !Utils.hasGpsPermissions(getActivity())) {
                if ((Constants.GPS_ENABLED) && !Utils.hasGpsPermissions(getActivity())) {
                    Log.e("aa","PERMS");
                    ActivityCompat.requestPermissions(getActivity(), Constants.gpsPermissions, 2);
                }
            }
        });
    }
}
