package edu.uw.covidsafe.symptoms;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.example.covidsafe.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.uw.covidsafe.ui.MainActivity;

public class SymptomUtils {

    public static void updateTodaysLogs(View view, List<SymptomsRecord> symptomRecords, Context cxt, Activity av, Date dateToShow) {
        ImageView amImage  = (ImageView)view.findViewById(R.id.amImage);
        ImageView pmImage  = (ImageView)view.findViewById(R.id.pmImage);
        TextView amStatus  = (TextView)view.findViewById(R.id.amStatus);
        TextView pmStatus  = (TextView)view.findViewById(R.id.pmStatus);
        ImageView amAction = view.findViewById(R.id.amAction);
        ImageView pmAction = view.findViewById(R.id.pmAction);

        SimpleDateFormat hour = new SimpleDateFormat("hh");
        SimpleDateFormat minute = new SimpleDateFormat("mm");

        amImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_edit));
        pmImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_edit));
        amStatus.setText("Not logged");
        pmStatus.setText("Not logged");

        amAction.setImageDrawable(cxt.getDrawable(R.drawable.ic_add_gray_24dp));
        pmAction.setImageDrawable(cxt.getDrawable(R.drawable.ic_add_gray_24dp));
        amAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAction(av, dateToShow, "am");
            }
        });
        pmAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAction(av, dateToShow, "pm");
            }
        });

        TextView featuredDate = (TextView)view.findViewById(R.id.featuredDate);
        SimpleDateFormat format2 = new SimpleDateFormat("EEEE, MMMM dd");
        featuredDate.setText(format2.format(dateToShow));

        Log.e("symptomutils","update show for "+format2.format(dateToShow));

        SimpleDateFormat day = new SimpleDateFormat("dd");
        SimpleDateFormat ampm = new SimpleDateFormat("aa");
        SimpleDateFormat outformat = new SimpleDateFormat("h:mm aa");

        for (SymptomsRecord record : symptomRecords) {
            Date symptomDate = new Date(record.getTs());
            String symptomDay = day.format(symptomDate);
            String dateToMatch = day.format(dateToShow);
            if (symptomDay.equals(dateToMatch)) {
                if (ampm.format(symptomDate).toLowerCase().equals("am")) {
                    amImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_done));
                    amStatus.setText(outformat.format(symptomDate));
                    amAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editAction();
                        }
                    });
                    amAction.setImageDrawable(cxt.getDrawable(R.drawable.ic_more_vert_gray_24dp));
                }
                else if (ampm.format(symptomDate).toLowerCase().equals("pm")) {
                    pmImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_done));
                    pmStatus.setText(outformat.format(symptomDate));
                    pmAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editAction();
                        }
                    });
                    pmAction.setImageDrawable(cxt.getDrawable(R.drawable.ic_more_vert_gray_24dp));
                }
            }
        }

//        Date d1 = new Date(symptomRecords.get(0).getTs());
//        Date d2 = new Date(symptomRecords.get(1).getTs());
//        symptomSetHelper(d1, view, cxt);
//        symptomSetHelper(d2, view, cxt);
    }

    public static void symptomSetHelper(Date d1, View view, Context cxt) {
        SimpleDateFormat outformat = new SimpleDateFormat("h:mm aa");
        SimpleDateFormat ampm = new SimpleDateFormat("aa");

        Date today = new Date();
        SimpleDateFormat day = new SimpleDateFormat("dd");
        String todayDate = day.format(today);

        ImageView amImage  = (ImageView)view.findViewById(R.id.amImage);
        ImageView pmImage  = (ImageView)view.findViewById(R.id.pmImage);
        TextView amStatus  = (TextView)view.findViewById(R.id.amStatus);
        TextView pmStatus  = (TextView)view.findViewById(R.id.pmStatus);

        ImageView amAction = view.findViewById(R.id.amAction);
        ImageView pmAction = view.findViewById(R.id.pmAction);

        if (day.format(d1).equals(todayDate)) {
            if (ampm.format(d1).toLowerCase().equals("am")) {
                amImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_done));
                amStatus.setText(outformat.format(d1));
                amAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editAction();
                    }
                });
                amAction.setImageDrawable(cxt.getDrawable(R.drawable.ic_more_vert_gray_24dp));
            }
            else if (ampm.format(d1).toLowerCase().equals("pm")){
                pmImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_done));
                pmStatus.setText(outformat.format(d1));
                pmAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editAction();
                    }
                });
                pmAction.setImageDrawable(cxt.getDrawable(R.drawable.ic_more_vert_gray_24dp));
            }
        }
    }

    public static void addAction(Activity av, Date date, String ampm) {
        FragmentTransaction tx = ((MainActivity)av).getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(
                R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,R.anim.exit_left_to_right);
        tx.replace(R.id.fragment_container, new AddSymptomsFragment(date, ampm)).commit();

    }

    public static void editAction() {
//        PopupMenu popup = new PopupMenu(this, v);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.actions, popup.getMenu());
//        popup.show();
    }
}
