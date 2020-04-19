package edu.uw.covidsafe.ui.health;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.uw.covidsafe.utils.Constants;

public class HealthPageAdapter extends FragmentPagerAdapter {

    public HealthPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return Constants.SymptomTrackerFragment;
        }
        else if (position == 1) {
            return Constants.DiagnosisFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Symptoms";
        }
        else if (position == 1) {
            return "Diagnosis";
        }
        return "";
    }
}
