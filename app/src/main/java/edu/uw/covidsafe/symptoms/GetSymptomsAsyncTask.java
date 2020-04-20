package edu.uw.covidsafe.symptoms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import java.util.Date;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;

import com.example.covidsafe.R;

public class GetSymptomsAsyncTask extends AsyncTask<Void, Void, Void> {

    Context cxt;
    List<SymptomsRecord> records;
    Activity av;

    public GetSymptomsAsyncTask(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

//        // update today's symptom status
//        ImageView amImage  = (ImageView)av.findViewById(R.id.amImage);
//        ImageView pmImage  = (ImageView)av.findViewById(R.id.pmImage);
//        TextView amStatus  = (TextView)av.findViewById(R.id.amStatus);
//        TextView pmStatus  = (TextView)av.findViewById(R.id.pmStatus);
//        TextView todayDate = (TextView)av.findViewById(R.id.todayDate);
//
//        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM dd");
//        todayDate.setText(format.format(new Date()));
//        if (records == null || this.records.size() == 0) {
//            amImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_edit));
//            pmImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_edit));
//            amStatus.setText("Not logged");
//            pmStatus.setText("Not logged");
//        }
//
//        SharedPreferences.Editor editor = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
//        editor.putLong(cxt.getString(R.string.last_symptom_update_pkey), TimeUtils.getTime());
//        editor.commit();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
