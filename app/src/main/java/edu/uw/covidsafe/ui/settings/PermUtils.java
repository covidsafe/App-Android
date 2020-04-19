package edu.uw.covidsafe.ui.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import static android.view.View.SCALE_X;
import static android.view.View.SCALE_Y;

public class PermUtils {

    static ObjectAnimator switchOnAnimator = null;
    static boolean stopit = true;

    public static void gpsSwitchLogic(Activity av) {
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();

        boolean hasGps = Utils.hasGpsPermissions(av);
        if (hasGps) {
            editor.putBoolean(av.getString(R.string.gps_enabled_pkey), true);
            editor.commit();
            if (!Constants.LoggingServiceRunning) {
                Utils.startLoggingService(av);
                GpsUtils.startGps(av);
                PermUtils.transition(false,av);
            }
            else {
                GpsUtils.startGps(av);
            }
        }
        else {
            Log.e("state","REQUEST GPS PERMS");
            ActivityCompat.requestPermissions(av, Constants.gpsPermissions, 2);
        }
    }

    public static void bleSwitchLogic(Activity av) {
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();

        BluetoothManager bluetoothManager =
                (BluetoothManager) av.getSystemService(Context.BLUETOOTH_SERVICE);
        Constants.blueAdapter = bluetoothManager.getAdapter();

        boolean hasBle = Utils.hasBlePermissions(av);

        if (hasBle && BluetoothUtils.isBluetoothOn(av)) {
            AppPreferencesHelper.setBluetoothEnabled(av);
            if (!Constants.LoggingServiceRunning) {
                Utils.startLoggingService(av);
                Log.e("ble","ble switch logic");
                BluetoothUtils.startBle(av);
                PermUtils.transition(false,av);
            }
            else {
                Log.e("ble","ble switch logic2");
                BluetoothUtils.startBle(av);
            }
        }
        else {
            if (Constants.blueAdapter != null && !Constants.blueAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                av.startActivityForResult(enableBtIntent, 0);
            }
            if (!Utils.hasBlePermissions(av)) {
                Log.e("results","no ble permission, let's request");
                ActivityCompat.requestPermissions(av, Utils.getBlePermissions(), 1);
            }
        }
    }

    public static void transition(boolean toOff, Activity av) {
        Log.e("transition","transition "+toOff);
        Drawable backgrounds[] = new Drawable[2];
        Resources res = av.getResources();
        if (toOff) {
            if (switchOnAnimator != null) {
                Log.e("times","cancel");
                switchOnAnimator.cancel();
                stopit = true;
            }

            backgrounds[0] = res.getDrawable(R.drawable.switch_on);
            backgrounds[1] = res.getDrawable(R.drawable.switch_off);
            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);

            ImageView image = (ImageView)av.findViewById(R.id.powerButton);
            if (image != null) {
                image.setImageDrawable(crossfader);
            }

            crossfader.startTransition(500);

            image = (ImageView)av.findViewById(R.id.ring);
            if (image != null) {
                PropertyValuesHolder a1 = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);
                final ObjectAnimator anim1 = ObjectAnimator.ofPropertyValuesHolder(image, a1);
                anim1.setDuration(500);
                anim1.start();
            }
        }
        else {
            backgrounds[0] = res.getDrawable(R.drawable.switch_off);
            backgrounds[1] = res.getDrawable(R.drawable.switch_on);
            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);

            ImageView image = (ImageView)av.findViewById(R.id.powerButton);
            if (image != null) {
                image.setImageDrawable(crossfader);

                crossfader.startTransition(500);
                stopit = false;
                animate(av);
            }
        }
    }

    public static void animate(Activity av) {
        ImageView image = (ImageView)av.findViewById(R.id.ring);
        PropertyValuesHolder a1 = PropertyValuesHolder.ofFloat(View.ALPHA, 0f,1f);
        final ObjectAnimator anim1 = ObjectAnimator.ofPropertyValuesHolder(image, a1);
        anim1.setDuration(1000);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(SCALE_X, 1f,1.5f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(SCALE_Y, 1f,1.5f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f,0f);
        switchOnAnimator = ObjectAnimator.ofPropertyValuesHolder(image, pvhX, pvhY,alpha);
        switchOnAnimator.setDuration(1000);
        switchOnAnimator.start();

        switchOnAnimator.addListener(new AnimatorListenerAdapter() {
            private boolean mCanceled;
            int times = 0;
            @Override
            public void onAnimationStart(Animator animation) {
                mCanceled = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("times","on cancel");
                mCanceled = true;
                image.setScaleX(1f);
                image.setScaleY(1f);
                image.setAlpha(0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mCanceled&&times<2) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!stopit) {
                                switchOnAnimator.start();
                                Log.e("times", times + "," + mCanceled);
                                times += 1;
                            }
                        }
                    }, 1000);
                }
                else if (!mCanceled && !stopit){
                    Log.e("times","stop");
                    image.setScaleX(1f);
                    image.setScaleY(1f);
                    anim1.start();
                }
            }
        });
    }
}
