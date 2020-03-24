package unused;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.corona.Constants;
import com.example.corona.FileOperations;
import com.example.corona.GpsRecord;
import com.example.corona.MainActivity;
import com.example.corona.R;
import com.example.corona.Utils;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.history_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Constants.CurrentFragment = this;
        Constants.HistoryFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.HistoryFragment = this;
        Constants.CurrentFragment = this;

        final String[] fileList = FileOperations.readfilelisthuman(getActivity());
        if (fileList != null) {
            Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
            spinner.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, fileList));
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Log.e("logme","SELECTED "+fileList[position]);
                    ArrayList<GpsRecord> records = FileOperations.readGpsRecords(getActivity(), "network", Utils.convertDate(fileList[position]));
                    displayPoints(records);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        }
    }

    public void displayPoints(ArrayList<GpsRecord> records) {
        
    }
}
