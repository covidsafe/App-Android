package edu.uw.covidsafe.comms;

import android.app.Activity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.gson.Gson;

public class NetworkConstant {
    public static String BASE_URL = "https://csapi.azurefd.net/api/";
    public static String API_KEY = "6755814d12ef46a2a7a206a8117abe45";
    public static int TIMEOUT = 10;
    static RequestQueue requestQueue;
    static Gson gson;

    public static void init(Activity av) {
        gson = new Gson();
        // Instantiate the cache
        Cache cache = new DiskBasedCache(av.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();
    }
}
