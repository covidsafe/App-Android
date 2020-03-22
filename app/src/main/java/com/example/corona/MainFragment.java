package com.example.corona;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;

import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    Button trackButton;
    String TAG = "err";
    TextView tv1;
    boolean tracking = false;
    LocationListener locationListener;
    TextView riskTv;

    /* Azure AD Variables */
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private IAccount mAccount;

    LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.main_header_text));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Constants.CurrentFragment = this;
        Constants.MainFragment = this;

//        locationManager = (LocationManager)
//                getActivity().getSystemService(getActivity().LOCATION_SERVICE);
//        locationListener = new MyLocationListener();

        tv1 = (TextView)getActivity().findViewById(R.id.textView);
        trackButton = (Button)getActivity().findViewById(R.id.trackButton);
        riskTv = (TextView)getActivity().findViewById(R.id.riskStatusTv);
        riskTv.setText(getString(R.string.risk_low));
        updateUI();

        trackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!tracking) {
                    try {
                        Log.e("logme", "request");
//                        locationManager.requestLocationUpdates(
//                                LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
                        getActivity().startService(new Intent(getActivity(), MyService.class));

                    } catch (SecurityException e) {
                        Log.e("log", e.getMessage());
                    }
                    tracking = true;
                }
                else {
                    locationManager.removeUpdates(locationListener);
                    tracking = false;
                }
                updateUI();
            }
        });
    }

    public void updateUI() {
        if (tracking) {
            trackButton.setText("Stop tracking");
            trackButton.setBackgroundResource(R.drawable.stopbutton);
        }
        else {
            trackButton.setText("Start tracking");
            trackButton.setBackgroundResource(R.drawable.startbutton);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.MainFragment = this;
        Constants.CurrentFragment = this;
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Log.e("logme","onlocationchanged");
            Log.e("logme",loc.getLatitude() +","+ loc.getLongitude()+","+Utils.time());

            boolean inBlacklist = Utils.locationInBlacklist(getActivity(), loc);
            Log.e("logme","in blacklist");
            if (!inBlacklist) {
                FileOperations.append(System.currentTimeMillis() + "," + loc.getLongitude() + "," + loc.getLongitude(),
                        getActivity(), Constants.gpsDirName, Utils.getLogName());
            }

            tv1.setText(Utils.time()+"\n"+loc.getLatitude()+"\n"+loc.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("logme","onproviderenabled");}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("logme","onstatuschanged");
        }
    }
}
