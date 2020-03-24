package unused;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.corona.utils.Constants;
import com.example.corona.utils.FileOperations;
import com.example.corona.ui.MainActivity;
import com.example.corona.R;
import com.example.corona.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    Button addButton;
    EditText addressText;
    TextView settingsHelperText;
    View view;
    RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme","HELP");
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.settings_header_text));
        return view;
    }

    MyAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        Constants.SettingsFragment = this;
        Constants.CurrentFragment = this;

        addButton = (Button) getActivity().findViewById(R.id.addButton);
        addressText = (EditText) getActivity().findViewById(R.id.addressText);
        settingsHelperText = (TextView) getActivity().findViewById(R.id.settingsHelperText);
        settingsHelperText.setText(getActivity().getString(R.string.settings_help_text, Constants.MaxBlacklistSize));

        refresh();

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Constants.blacklist.size() > Constants.MaxBlacklistSize) {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                            .setTitle("Error")
                            .setMessage(getActivity().getString(R.string.maxBlacklistSizeError))
                            .setPositiveButton(R.string.ok, null)
                            .setCancelable(false).create();
                }
                else {
                    String address = addressText.getText().toString();
                    double[] gps = Utils.address2gps(getActivity(), address);
                    if (gps == null) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                                .setTitle("Error")
                                .setMessage(getActivity().getString(R.string.addressError))
                                .setPositiveButton(R.string.ok, null)
                                .setCancelable(false).create();
                        dialog.show();
                    } else {
                        FileOperations.append(gps[0] + "," + gps[1] + "," + address, getActivity(), Constants.blacklistDirName, Constants.blacklistFileName);
                        Utils.mkSnack(getActivity(), view, "Address successfully added");
                        refresh();
                    }
                }
            }
        });
    }

    public void refresh() {
        Constants.blacklist = FileOperations.readBlacklist(getActivity());
        if (Constants.blacklist != null) {
            List<String> blacklistAddresses = new ArrayList<String>();
            for (BlacklistRecord record : Constants.blacklist) {
                blacklistAddresses.add(record.address);
            }

            RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new MyAdapter(blacklistAddresses);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            // do whatever
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);

//            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//                @Override
//                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                    Log.e("logme","onmove");
//                    return false;
//                }
//
//                @Override
//                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                    //Remove swiped item from list and notify the RecyclerView
//                    Log.e("logme","swiped");
//                }
//            };
//
//            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.SettingsFragment = this;
        Constants.CurrentFragment = this;
    }
}
