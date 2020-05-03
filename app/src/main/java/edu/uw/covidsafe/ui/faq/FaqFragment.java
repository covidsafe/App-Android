package edu.uw.covidsafe.ui.faq;

import android.annotation.SuppressLint;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.util.ArrayList;
import java.util.List;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;

public class FaqFragment extends Fragment {

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme","HELP");
        View view = inflater.inflate(R.layout.fragment_faq2, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getColor(R.color.white));
        }

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();

        String header_str = getActivity().getString(R.string.help_header_text);
        if (Constants.PUBLIC_DEMO) {
            header_str = getActivity().getString(R.string.help_header_text_demo);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(header_str));

        List<String> questions = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        questions.add(getString(R.string.q1));
        answers.add(getString(R.string.a1));
        questions.add(getString(R.string.q2));
        answers.add(getString(R.string.a2));
        questions.add(getString(R.string.q3));
        answers.add(getString(R.string.a3));
        questions.add(getString(R.string.q4));
        answers.add(getString(R.string.a4));
        questions.add(getString(R.string.q5));
        answers.add(getString(R.string.a5));

        //////////////////////////////////////////////////////////////////

        List<String> questions2 = new ArrayList<>();
        List<String> answers2 = new ArrayList<>();
        questions2.add(getString(R.string.pq1));
        answers2.add(getString(R.string.pa1));
        questions2.add(getString(R.string.pq2));
        answers2.add(getString(R.string.pa2));
        questions2.add(getString(R.string.pq3));
        answers2.add(getString(R.string.pa3));
        questions2.add(getString(R.string.pq4));
        answers2.add(getString(R.string.pa4));
        questions2.add(getString(R.string.pq5));
        answers2.add(getString(R.string.pa5));
        questions2.add(getString(R.string.pq6));
        answers2.add(getString(R.string.pa6));
        questions2.add(getString(R.string.pq7));
        answers2.add(getString(R.string.pa7));
        questions2.add(getString(R.string.pq8));
        answers2.add(getString(R.string.pa8));

        //////////////////////////////////////////////////////////////////

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewResources);
        ResourceRecyclerViewAdapter resAdapter = new ResourceRecyclerViewAdapter(getContext(),getActivity());
        recyclerView.setAdapter(resAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView recyclerView1 = view.findViewById(R.id.recyclerView1);
        FaqRecyclerView faqAdapter = new FaqRecyclerView(getContext());
        recyclerView1.setAdapter(faqAdapter);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
        faqAdapter.setData(questions,answers);

        RecyclerView recyclerView2 = view.findViewById(R.id.recyclerView2);
        FaqRecyclerView faqAdapter2 = new FaqRecyclerView(getContext());
        recyclerView2.setAdapter(faqAdapter2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        faqAdapter2.setData(questions2,answers2);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("logme","HELP");
        Constants.FaqFragment = this;
        Constants.CurrentFragment = this;

        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
            Constants.menu.findItem(R.id.mybutton).setVisible(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.FaqFragment = this;
        Constants.CurrentFragment = this;
    }
}
