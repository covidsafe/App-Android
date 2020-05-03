package edu.uw.covidsafe.ui.contact_log;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.List;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import edu.uw.covidsafe.contact_trace.HumanDbModel;
import edu.uw.covidsafe.contact_trace.HumanRecord;
import edu.uw.covidsafe.contact_trace.HumanSummaryRecyclerViewAdapter;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

public class PeopleFragment extends Fragment {

    View view;
    boolean humanDbChanged;
    HumanSummaryRecyclerViewAdapter humanAdapter;
    static List<HumanRecord> changedRecords;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Constants.CurrentFragment = this;
        view = inflater.inflate(R.layout.fragment_people, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getColor(R.color.white));
        }

        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
            Constants.menu.findItem(R.id.mybutton).setVisible(true);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        FloatingActionButton actionButton = (FloatingActionButton)view.findViewById(R.id.fabButton);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 0);
                }
                else {
//                    Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                    pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//                    ((MainActivity) getActivity()).startActivityForResult(pickContact, 2);
                    selectContacts();
                }
            }
        });

        String header_str = getActivity().getString(R.string.contact_log_header_text);
        if (Constants.PUBLIC_DEMO) {
            header_str = getActivity().getString(R.string.contact_log_header_text_demo);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(header_str));

        humanAdapter = new HumanSummaryRecyclerViewAdapter(getContext(), getActivity(), view);
        RecyclerView tipView = view.findViewById(R.id.recyclerViewPeople);
        tipView.setAdapter(humanAdapter);
        tipView.setLayoutManager(new LinearLayoutManager(getActivity()));

        HumanDbModel smodel = ViewModelProviders.of(getActivity()).get(HumanDbModel.class);
        smodel.getAllSorted().observe(getActivity(), new Observer<List<HumanRecord>>() {
            @Override
            public void onChanged(List<HumanRecord> humanRecords) {
                Log.e("human","onchanged "+humanRecords.size());
                humanDbChanged = true;
                Constants.changedContactHumanRecords = humanRecords;
//                if (Constants.CurrentFragment.toString().toLowerCase().contains("people")) {
                    humanAdapter.setRecords(humanRecords);
                    humanDbChanged = false;
//                }
            }
        });

        return view;
    }

    private void selectContacts() {
        new MultiContactPicker.Builder(getActivity()) //Activity/fragment context
                .theme(R.style.MyCustomPickerTheme)//Optional - default: MultiContactPicker.Azure
                .hideScrollbar(true) //Optional - default: false
                .showTrack(false) //Optional - default: true
                .searchIconColor(Color.BLACK) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE

                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .setTitleText(getContext().getString(R.string.select_contact)) //Optional - default: Select Contacts
                //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(3);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.PeopleFragment = this;
        Constants.CurrentFragment = this;

        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
            Constants.menu.findItem(R.id.mybutton).setVisible(false);
        }
        if (humanDbChanged) {
            Log.e("contact","db changed ");
            humanAdapter.setRecords(changedRecords);
            humanDbChanged = false;
        }
    }
}
