package edu.uw.covidsafe.gps;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.covidsafe.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ImportLocationHistoryFragment extends Fragment {

    View view;

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.e("log","DONE");
            Utils.mkSnack(getActivity(), view, getContext().getString(R.string.download_complete));

            LocationDataXMLParser parser = new LocationDataXMLParser();
            parser.getLinks(context, context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
        }
    };

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme", "HELP");
        view = inflater.inflate(R.layout.fragment_import, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getColor(R.color.white));
        }

        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
            Constants.menu.findItem(R.id.mybutton).setVisible(false);
        }

        getContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(getActivity().getDrawable(R.drawable.ic_close_black_24dp));

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.settings_header_text)));

        final String signin_url = "https://accounts.google.com/signin/v2/identifier?flowName=GlifWebSignIn&flowEntry=ServiceLogin";

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        Date now = new Date(TimeUtils.getTime());
        Date begin = null;
        if (Constants.DEBUG) {
            begin = new Date(TimeUtils.getNDaysForward(-Constants.DefaultInfectionWindowInDaysDebug));
        }
        else {
            begin = new Date(TimeUtils.getNDaysForward(-Constants.DefaultInfectionWindowInDays));
        }

        final String download_url = "https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i" +
                yearFormat.format(begin) + "!2i" + monthFormat.format(begin) + "!3i" + dayFormat.format(begin) +
                "!2m3!1i" + yearFormat.format(now) + "!2i" + monthFormat.format(now) + "!3i" + dayFormat.format(now);

        final WebView myWebView = (WebView) view.findViewById(R.id.webview);

        final Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/vnd.google-earth.kml+xml");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(signin_url);

        myWebView.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(myWebView, url);
                Log.e("log","on page finished "+url);
                if (url.contains(signin_url)) {
                    Log.e("log","----------------------");
                }
                else if (url.contains("https://myaccount.google.com/?utm_source=sign_in_no_continue")) {
                    myWebView.setVisibility(View.GONE);
                    myWebView.clearFormData();
                    myWebView.clearCache(true);
                    myWebView.loadUrl(download_url, headers);
                    Log.e("log","*************************");
                }
            }
        });

        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Log.e("log","set download listener");
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                String cookie = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("Cookie", cookie);
                request.addRequestHeader("content-disposition", "attachment");
                request.setMimeType("application/vnd.google-earth.kml+xml");
                request.addRequestHeader("content-type", "application/vnd.google-earth.kml+xml");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOWNLOADS, "covidSafe_loc_history.kml");
                DownloadManager dm = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Utils.mkSnack(getActivity(), view, getContext().getString(R.string.downloading));

                FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(
                        R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                        R.anim.enter_left_to_right,R.anim.exit_left_to_right);
                tx.replace(R.id.fragment_container, Constants.SettingsFragment).commit();
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
//        Constants.SettingsFragment = this;
        Constants.CurrentFragment = this;
//        Constants.MainFragmentState = this;

        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
            Constants.menu.findItem(R.id.mybutton).setVisible(false);
        }
        Log.e("perms", "should update switch states? " + Constants.SuppressSwitchStateCheck);
        if (Constants.SuppressSwitchStateCheck) {
            Constants.SuppressSwitchStateCheck = false;
        } else {
            Utils.updateSwitchStates(getActivity());
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl (request.getUrl().toString());
            return true;
        }
        //        @Override
//        //show the web page in webview but not in web browser
//        public boolean shouldOverrideUrlLoading(@org.jetbrains.annotations.NotNull WebView view, String url) {
//            view.loadUrl (url);
//            return true;
//        }
    }
}