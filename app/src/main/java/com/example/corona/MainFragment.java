package com.example.corona;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    Button startButton;
    Button stopButton;
    String TAG = "err";
    TextView tv1;
    boolean tracking = false;
    LocationListener locationListener;

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

        locationManager = (LocationManager)
                getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        tv1 = (TextView)getActivity().findViewById(R.id.textView);
        startButton = (Button)getActivity().findViewById(R.id.startButton);
        stopButton = (Button)getActivity().findViewById(R.id.stopButton);
        updateUI();

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.e("logme","request");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
                }
                catch(SecurityException e) {
                    Log.e("log",e.getMessage());
                }
                tracking = true;
                updateUI();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                locationManager.removeUpdates(locationListener);
                tracking = false;
                updateUI();
            }
        });
    }

    public void updateUI() {
        if (tracking) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
        else {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
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
            Log.e("logme",loc.getLatitude() +","+ loc.getLongitude());
            FileOperations.append(System.currentTimeMillis()+","+loc.getLongitude()+","+loc.getLongitude(), getActivity());

            /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (Exception e) {
                Log.e("logme",e.getMessage());
            }
            Log.e("logme",cityName);
            tv1.setText(Utils.time()+"\n"+loc.getLatitude()+"\n"+loc.getLongitude()+"\n"+cityName);
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
