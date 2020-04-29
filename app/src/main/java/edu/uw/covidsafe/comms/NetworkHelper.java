package edu.uw.covidsafe.comms;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
                HashMap<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/json");
                headers.put("Accept","application/json");
                return headers;
            }
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError != null && volleyError.networkResponse != null) {
                    int statusCode = volleyError.networkResponse.statusCode;
                    switch (statusCode) {
                        case 500:
                            Log.e("err","err is 500");
                            return null;
                        default:
                            Log.e("err","err is "+statusCode);
                            return null;
                    }
                }
                return volleyError;
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Log.e("net","parse response size "+response.data.length);
                    Log.e("net","parse status code "+response.statusCode);
                    // response is just empty, return an empty json object
                    if (response.data.length == 0) {
                        String payload = "{statusCode: "+response.statusCode+"}";
                        byte[] responseData = payload.getBytes("UTF8");
                        if (method == Request.Method.HEAD) {
                            if (response.headers.containsKey("Content-Length")) {
                                int len = Integer.parseInt(response.headers.get("Content-Length"));
                                String json = "{statusCode: "+response.statusCode+", sizeOfQueryResponse:"+len+"}";
                                responseData = json.getBytes("UTF-8");
                            }
                        }
                        response = new NetworkResponse(response.statusCode, responseData, response.headers, response.notModified);
                    }
                    else {
                        try {
                            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            Log.e("net",jsonString);

                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(jsonString);
                                obj.put("statusCode",response.statusCode);
                            }
                            catch (JSONException e) {
                                Log.e("err",e.getMessage());
                                try {
                                    JSONArray arr = new JSONArray(jsonString);
                                    obj = new JSONObject();
                                    obj.put("results", arr);
                                    obj.put("statusCode",response.statusCode);
                                }
                                catch(JSONException e2) {
                                    Log.e("err",e2.getMessage());
                                }
                            }
                            return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        }
//                        catch (JSONException je) {
//                            return Response.error(new ParseError(je));
//                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        }
        ;

        NetworkConstant.requestQueue.add(request);

        JSONObject response = null;
        try {
            response = future.get(NetworkConstant.TIMEOUT, TimeUnit.SECONDS); // this will block
        } catch (InterruptedException e) {
            Log.e("net","11 "+e.getMessage());
        } catch (ExecutionException e) {
            Log.e("net","22 "+e.getMessage());
        } catch (TimeoutException e) {
            Log.e("net","33 "+(e.toString())+"");
        }
        if (response != null) {
            Log.e("net", "finished request " + response.toString());
        }
        else {
            Log.e("net", "finished request ");
        }
        return response;
    }

    public static boolean isNetworkAvailable(Activity av) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) av.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
