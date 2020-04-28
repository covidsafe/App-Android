package edu.uw.covidsafe.ui.contact_log;

import android.content.Context;

import com.example.covidsafe.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.uw.covidsafe.utils.Constants;

public class ContactLogPageAdapter extends FragmentPagerAdapter {
    Context cxt;
    public ContactLogPageAdapter(@NonNull FragmentManager fm, Context cxt) {
        super(fm);
        this.cxt = cxt;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return Constants.LocationFragment;
        }
        else if (position == 1) {
            return Constants.PeopleFragment;
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
            return cxt.getString(R.string.locations_text);
        }
        else if (position == 1) {
            return cxt.getString(R.string.people_text);
        }
        return "";
    }
}
