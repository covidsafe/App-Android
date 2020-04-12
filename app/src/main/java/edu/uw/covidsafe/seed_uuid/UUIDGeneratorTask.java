package edu.uw.covidsafe.seed_uuid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ble.BluetoothScanHelper;
import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.Utils;

import java.util.UUID;

public class UUIDGeneratorTask implements Runnable {

    Context context;

    public UUIDGeneratorTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.e("ble","generate uuid");

        // get the most recently generated seed
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        String mostRecentSeedStr = prefs.getString(context.getString(R.string.most_recent_seed_pkey), "");
        byte[] mostRecentSeed = ByteUtils.string2byteArray(mostRecentSeedStr);

        SeedUUIDRecord generatedRecord = CryptoUtils.generateSeed(context, mostRecentSeed);
        if (generatedRecord != null) {
            Constants.contactUUID = UUID.fromString(generatedRecord.getUUID());
        }

        if (Constants.blueAdapter != null && Constants.blueAdapter.getBluetoothLeAdvertiser() != null) {
            Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothScanHelper.advertiseCallback);

            //restart beacon after UUID generation
            BluetoothUtils.mkBeacon();
        }
    }
}
