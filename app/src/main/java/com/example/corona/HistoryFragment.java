package com.example.corona;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        String[] fileList = FileOperations.readfilelist(getActivity(), Constants.gpsDirName);

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, fileList));
    }
}
