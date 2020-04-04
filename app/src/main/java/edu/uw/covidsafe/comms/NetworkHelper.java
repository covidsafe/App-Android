package edu.uw.covidsafe.comms;

import android.content.Context;
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
                    headers.put("Ocp-Apim-Subscription-Key","6755814d12ef46a2a7a206a8117abe45");
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
            Log.e("net",e.getMessage());
        } catch (ExecutionException e) {
            Log.e("net",e.getMessage());
        } catch (TimeoutException e) {
            Log.e("net",(e.toString())+"");
        }
        Log.e("net","finished request");
        return response;
    }
}
