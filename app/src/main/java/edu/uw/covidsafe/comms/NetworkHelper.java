package edu.uw.covidsafe.comms;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

public class NetworkHelper {
    public static void sendRecords(final JsonObject obj, Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //TODO change url and also in network_security_config.xml
                    InetSocketAddress addr = new InetSocketAddress(NetworkConstant.HOSTNAME,NetworkConstant.PORT);
                    URL url = new URL("http://"+addr.toString()+"/companies");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//                    os.writeBytes(URLEncoder.encode(records.toJson().toString(), "UTF-8"));
                    os.writeBytes(obj.toString());

                    os.flush();
                    os.close();

                    int resp = conn.getResponseCode();
                    if (resp != 200) {
                        Toast.makeText(context,"Failed to send records. Please try again.", Toast.LENGTH_LONG);
                    }
                    Log.e("STATUS", String.valueOf(resp));
                    Log.e("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e("logme",e.getMessage());
                }
            }
        });

        thread.start();
    }
}
