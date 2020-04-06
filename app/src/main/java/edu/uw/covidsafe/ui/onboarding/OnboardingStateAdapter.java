package edu.uw.covidsafe.ui.onboarding;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.covidsafe.R;

import edu.uw.covidsafe.utils.Constants;

public class OnboardingStateAdapter extends FragmentPagerAdapter {

    public OnboardingStateAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        StoryFragment frag = new StoryFragment();
        Bundle bundle = new Bundle();
//        Log.e("STATE","page number "+position);
        Constants.pageNumber = position;
        if (position==0) {
            bundle.putInt("image", R.drawable.onboard1);
            bundle.putInt("message", R.string.story1);
        }
        else if (position==1) {
            bundle.putInt("image", R.drawable.onboard2);
            bundle.putInt("message", R.string.story2);
        }
        else if (position==2) {
            bundle.putInt("image", R.drawable.onboard3);
            bundle.putInt("message", R.string.story3);
        }
        else if (position==3) {
            bundle.putInt("image", R.drawable.onboard4);
            bundle.putInt("message", R.string.story4);
        }
        bundle.putInt("pos",position);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
