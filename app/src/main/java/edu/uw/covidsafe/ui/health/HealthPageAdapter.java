package edu.uw.covidsafe.ui.health;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.covidsafe.R;

import edu.uw.covidsafe.utils.Constants;

public class HealthPageAdapter extends FragmentPagerAdapter {
    Context cxt;
    public HealthPageAdapter(@NonNull FragmentManager fm, Context cxt) {
        super(fm);
        this.cxt = cxt;
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
            return cxt.getString(R.string.symptoms_text);
        }
        else if (position == 1) {
            return cxt.getString(R.string.diagnosis_text);
        }
        return "";
    }
}
