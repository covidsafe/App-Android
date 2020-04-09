package edu.uw.covidsafe.hcp;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.List;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.json.Area;
import edu.uw.covidsafe.json.AreaMatch;

public class SubmitNarrowcastMessageTask extends AsyncTask<Void, Void, Void> {

    Activity av;
    View view;
    List<Double> lats;
    List<Double> longs;
    List<Float> radii;
    String msg;

    public SubmitNarrowcastMessageTask(Activity av, View view,
                                       List<Double> lats, List<Double> longs,
                                       List<Float> radii, String msg) {
        this.av = av;
        this.view = view;
        this.lats = lats;
        this.longs = longs;
        this.radii = radii;
        this.msg = msg;
    }

    public AreaMatch packageData() {
        AreaMatch amatch = new AreaMatch();
        amatch.userMessage = this.msg;
        amatch.areas = new Area[lats.size()];
        for (int i = 0; i < lats.size(); i++) {
            amatch.areas[i] = new Area(lats.get(i),longs.get(i),radii.get(i),
                    System.currentTimeMillis(),System.currentTimeMillis());
        }

        return amatch;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("narrowcast","SubmitNarrowcastMessageTask");
        String url = AreaMatch.toHttpString();

        AreaMatch areaMatch = packageData();

        JSONObject matchMessageObj = null;
        try {
            matchMessageObj = areaMatch.toJson();
        }
        catch (Exception e) {
            Log.e("err",e.getMessage());
        }
        if (matchMessageObj == null) {
            return null;
        }

        Log.e("narrowcast","sendreqest");
        try {
            Log.e("narrowcast", matchMessageObj.toString(2));
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
        JSONObject resp = NetworkHelper.sendRequest(url, Request.Method.PUT, matchMessageObj);
        if (resp == null) {
            mkSnack(av, view, "There was an error with submitting your message. Please try again later.");
            return null;
        }

        Log.e("narrowcast","message submitted");
        mkSnack(av, view, "Your message has been sent.");

        return null;
    }

    public static void mkSnack(Activity av, View v, String msg) {
        av.runOnUiThread(new Runnable() {
            public void run() {
                final Snackbar snackBar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);

                snackBar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                    }
                });

                View snackbarView = snackBar.getView();
                TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setMaxLines(5);

                snackBar.show();
            }});
    }
}
