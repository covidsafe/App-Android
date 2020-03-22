package com.example.corona;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WarningFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme","WARNING");
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.warning_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("logme","WARNING");
        Constants.WarningFragment = this;
        Constants.CurrentFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.WarningFragment = this;
        Constants.CurrentFragment = this;
    }
}
