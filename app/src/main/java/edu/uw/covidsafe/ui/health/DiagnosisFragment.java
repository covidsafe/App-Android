package edu.uw.covidsafe.ui.health;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.comms.SendInfectedUserData;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class DiagnosisFragment extends Fragment {

    Context cxt;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_diagnosis, container, false);
        this.cxt = getContext();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final Drawable upArrow = getActivity().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getActivity().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);

        String header_str = getActivity().getString(R.string.health_header_text);
        if (Constants.PUBLIC_DEMO) {
            header_str = getActivity().getString(R.string.health_header_text_demo);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(header_str));

        RecyclerView rview = view.findViewById(R.id.recyclerViewTipsDiagnosis);
        rview.setAdapter(Constants.DiagnosisTipAdapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));
        Constants.DiagnosisTipAdapter.enableTips(1,view,true);

        CheckBox certBox = (CheckBox) view.findViewById(R.id.certBoxReport);
        Button uploadButton = (Button)view.findViewById(R.id.uploadButton);
        if (uploadButton != null) {
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constants.PUBLIC_DEMO) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                                .setMessage(getString(R.string.demo_disabled))
                                .setPositiveButton(getString(R.string.ok),null)
                                .setCancelable(true).create();
                        dialog.show();
                        return;
                    }

                    if (!NetworkHelper.isNetworkAvailable(getActivity())) {
                        Utils.mkSnack(getActivity(),view,getString(R.string.network_down));
                    }
                    else {
                        if (!certBox.isChecked()) {
                            AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                                    .setMessage(getString(R.string.check_box))
                                    .setNegativeButton(getString(R.string.cancel),null)
                                    .setPositiveButton(getString(R.string.ok),null)
                                    .setCancelable(true).create();
                            dialog.show();
                        }
                        else {
                            if (Constants.UI_AUTH) {
                                final EditText input = new EditText(getContext());

                                input.setInputType(InputType.TYPE_CLASS_TEXT);

                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity())
                                        .setMessage(getString(R.string.confirm_diagnosis_sure))
                                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Utils.mkSnack(getActivity(), view, getString(R.string.diag_not_submitted));
                                            }
                                        })
                                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String txt = input.getText().toString();
                                                if (txt.trim().equals(getString(R.string.confirm))) {
                                                    new SendInfectedUserData(getContext(), getActivity(), view).execute();
                                                } else {
                                                    Utils.mkSnack(getActivity(), view, getString(R.string.diag_not_submitted));
                                                }
                                            }
                                        })
                                        .setCancelable(true);

                                builder.setView(input);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            else {
                                new SendInfectedUserData(getContext(), getActivity(), view).execute();
                            }
                        }
                    }
                }
            });
        }

        Button whatHappens = (Button)view.findViewById(R.id.whatHappens);
        if (whatHappens != null) {
            whatHappens.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                            .setMessage(getString(R.string.confirm_diagnosis))
                            .setNegativeButton(getString(R.string.privacyText), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.covidSiteLink)));
                                    startActivity(browserIntent);
                                }
                            })
                            .setPositiveButton(getString(R.string.dismiss_text), null)
                            .setCancelable(false).create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.darkGray));
                }
            });
        }

        Button prepForInterview = (Button)view.findViewById(R.id.prep);
        if (prepForInterview != null) {
            prepForInterview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
                    tx.setCustomAnimations(
                            R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                    Constants.ContactPageNumber = 0;
                    tx.replace(R.id.fragment_container, Constants.ContactTraceFragment).commit();
                }
            });
        }
        Button learnMore = (Button)view.findViewById(R.id.learnMore);
        if (learnMore != null) {
            learnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constants.PUBLIC_DEMO) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                                .setMessage(getString(R.string.demo_disabled))
                                .setPositiveButton(getString(R.string.ok),null)
                                .setCancelable(true).create();
                        dialog.show();
                        return;
                    }
                    else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.kingCountyLink)));
                        startActivity(browserIntent);
                    }
                }
            });
        }

        RecyclerView rview2 = view.findViewById(R.id.recyclerViewResources);
        ResourceRecyclerViewAdapter adapter2 = new ResourceRecyclerViewAdapter(getContext(),getActivity());
        rview2.setAdapter(adapter2);
        rview2.setLayoutManager(new LinearLayoutManager(getActivity()));

        DiagnosisFragment.updateSubmissionView(getActivity(), getContext(), view, false);

        return view;
    }

    public static void updateSubmissionView(Activity av, Context context, View view, boolean justReported) {
        av.runOnUiThread(new Runnable() {
            public void run() {
                TextView pos = view.findViewById(R.id.pos);
                TextView date = view.findViewById(R.id.date);
                if (justReported) {
                    Log.e("state","VISIBLE");
//                    pos.setText(context.getString(R.string.pos_text));
//                    pos.setVisibility(View.VISIBLE);
//                    date.setText("");
//                    date.setVisibility(View.GONE);
//                    textView7
                }
                else {
                    pos.setText("");
                    pos.setVisibility(View.GONE);
                    date.setText("");
                    date.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.DiagnosisFragment = this;
        Constants.HealthFragmentState = this;
        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
            Constants.menu.findItem(R.id.mybutton).setVisible(true);
        }
    }
}
