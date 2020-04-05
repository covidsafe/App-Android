package edu.uw.covidsafe.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.covidsafe.R;

import edu.uw.covidsafe.utils.Constants;

public class PagerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_story, container, false);
        ViewPager viewPager = view.findViewById(R.id.pager);
        OnboardingStateAdapter adapter = new OnboardingStateAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        if (Constants.pageNumber != -1) {
            viewPager.setCurrentItem(Constants.pageNumber);
        }
        return view;
    }
}
