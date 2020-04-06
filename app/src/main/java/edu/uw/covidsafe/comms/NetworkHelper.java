package edu.uw.covidsafe.comms;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NetworkHelper {

    public static JSONObject sendRequest(String url, int method, JSONObject obj) {
        Log.e("net","start request");
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(method, url, obj, future, future)
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                if (obj != null) {
                    HashMap<String,String> headers = new HashMap<String, String>();
                    headers.put("Content-Type","application/json-patch+json");
                    headers.put("Ocp-Apim-Subscription-Key",NetworkConstant.API_KEY);
                    return headers;
                }
                else {
                    return new HashMap<String, String>();
                }
            }
        }
        ;

        NetworkConstant.requestQueue.add(request);

        JSONObject response = null;
        try {
            response = future.get(NetworkConstant.TIMEOUT, TimeUnit.SECONDS); // this will block
            Log.e("net",response.toString());
        } catch (InterruptedException e) {
            Log.e("net","11 "+e.getMessage());
        } catch (ExecutionException e) {
            Log.e("net","22 "+e.getMessage());
        } catch (TimeoutException e) {
            Log.e("net","33 "+(e.toString())+"");
        }
        Log.e("net","finished request");
        return response;
    }

    public static boolean isNetworkAvailable(Activity av) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) av.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
