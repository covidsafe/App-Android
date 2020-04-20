package edu.uw.covidsafe.symptoms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.FragmentTransaction;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

public class SymptomUtils {

    public static void updateTodaysLogs(View view, List<SymptomsRecord> symptomRecords, Context cxt, Activity av, Date dateToShow, String entryPoint) {
        Log.e("symptom","update todays log "+entryPoint);
        Constants.entryPoint = entryPoint;
        ImageView amImage  = (ImageView)view.findViewById(R.id.amImage);
        ImageView pmImage  = (ImageView)view.findViewById(R.id.pmImage);
        TextView amStatus  = (TextView)view.findViewById(R.id.amStatus);
        TextView pmStatus  = (TextView)view.findViewById(R.id.pmStatus);
        ImageView amAction = view.findViewById(R.id.amAction);
        ImageView pmAction = view.findViewById(R.id.pmAction);

        SimpleDateFormat hour = new SimpleDateFormat("hh");
        SimpleDateFormat minute = new SimpleDateFormat("mm");

        // view elements are null
        Log.e("ddebug ",(cxt==null)+"");
        amImage.setImageDrawable(cxt.getResources().getDrawable(R.drawable.symptom_edit));
        pmImage.setImageDrawable(cxt.getResources().getDrawable(R.drawable.symptom_edit));
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
        SimpleDateFormat outformat = new SimpleDateFormat("MM/dd h:mm aa");

        for (SymptomsRecord record : symptomRecords) {
            Date symptomLogDate = new Date(record.getLogTime());
            Date symptomActualDate = new Date(record.getTs());

            String symptomDay = day.format(symptomActualDate);
            String dateToMatch = day.format(dateToShow);

            if (symptomDay.equals(dateToMatch)) {
                if (ampm.format(symptomActualDate).toLowerCase().equals("am")) {
                    amImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_done));
                    amStatus.setText("Logged: "+outformat.format(symptomLogDate));
                    Log.e("symtom","edit am action to "+dateToMatch);
                    amAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editAction(cxt,  av, amAction, record);
                        }
                    });
                    amAction.setImageDrawable(cxt.getDrawable(R.drawable.ic_more_vert_gray_24dp));
                }
                else if (ampm.format(symptomActualDate).toLowerCase().equals("pm")) {
                    pmImage.setImageDrawable(cxt.getDrawable(R.drawable.symptom_done));
                    pmStatus.setText("Logged: "+outformat.format(symptomLogDate));
                    Log.e("symtom","edit pm action to "+dateToMatch);
                    pmAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editAction(cxt, av, pmAction, record);
                        }
                    });
                    pmAction.setImageDrawable(cxt.getDrawable(R.drawable.ic_more_vert_gray_24dp));
                }
            }
        }
    }

    public static void addAction(Activity av, Date date, String ampm) {
        FragmentTransaction tx = ((MainActivity)av).getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(
                R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                R.anim.enter_left_to_right,R.anim.exit_left_to_right);
        tx.replace(R.id.fragment_container, new AddEditSymptomsFragment(date, ampm)).commit();
    }

    public static void editAction(Context context, Activity av, View view, SymptomsRecord record) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
//        ((MenuBuilder)popup.getMenu()).setOptionalIconsVisible(true);
        inflater.inflate(R.menu.overflow_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editItem:
                        FragmentTransaction tx = ((MainActivity) av).getSupportFragmentManager().beginTransaction();
                        tx.setCustomAnimations(
                                R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                        tx.replace(R.id.fragment_container, new AddEditSymptomsFragment(record)).commit();
                        break;
                    case R.id.deleteItem:
                        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                                .setTitle("Are you sure you want to delete?")
                                .setNegativeButton("Cancel",null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new SymptomsOpsAsyncTask(Constants.SymptomsDatabaseOps.Delete, context, record).execute();
                                    }
                                })
                                .setCancelable(true).create();
                        dialog.show();
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}
