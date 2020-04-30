package com.example.importlocations;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="AndroidRide";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Takeout page: https://takeout.google.com/settings/takeout/custom/location_history
        // KML page: "https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i" + year + "!2i" + month + "!3i" + mday +
        //        "!2m3!1i" + year + "!2i" + month + "!3i" + mday;
        // Ex: "https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i" + 2020 + "!2i" + 01 + "!3i" + 01 +
        //                "!2m3!1i" + 2020 + "!2i" + 04 + "!3i" + 01
        final String signin_url = "https://accounts.google.com/signin/v2/identifier?flowName=GlifWebSignIn&flowEntry=ServiceLogin";
        final String download_url = "https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i" + 2020 + "!2i" + 01 + "!3i" + 01 +
                "!2m3!1i" + 2020 + "!2i" + 01 + "!3i" + 04;
        final Map<String, String> headers = new HashMap<>();
        final WebView myWebView = (WebView) findViewById(R.id.webview);

        headers.put("content-type", "application/vnd.google-earth.kml+xml");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(myWebView, url);
                if (url.contains("https://myaccount.google.com/")) {
                    checkStoragePermission();
                    myWebView.loadUrl(download_url, headers);
                    myWebView.setVisibility(View.GONE);
                    myWebView.clearFormData();
                    myWebView.clearCache(true);
                    try {
                        Thread.sleep(5000);
                        System.out.println("Awake");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    LocationDataXMLParser parser = new LocationDataXMLParser();
                    List<List<String>> output = parser.getLinks(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
                    for(List<String> section : output) {
                        for(String text : section) {
                            System.out.println("Entry:" + text);
                        }
                    }
                }
            }
        });

        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                String cookie = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("Cookie", cookie);
                request.addRequestHeader("content-disposition", "attachment");
                request.setMimeType("application/vnd.google-earth.kml+xml");
                request.addRequestHeader("content-type", "application/vnd.google-earth.kml+xml");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "covidSafe_loc_history");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(context, "Downloading File", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();
            }
        });

        myWebView.loadUrl(signin_url);







    }

    public  void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        //show the web page in webview but not in web browser
        public boolean shouldOverrideUrlLoading(@org.jetbrains.annotations.NotNull WebView view, String url) {
            view.loadUrl (url);
            return true;
        }
    }






}
