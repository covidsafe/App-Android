package edu.uw.covidsafe.ui.onboarding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class StoryFragment extends Fragment {

    ImageView imageView;
    TextView contentView;
    TextView titleView;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        ((OnboardingActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((OnboardingActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((OnboardingActivity) getActivity()).getSupportActionBar().hide();

        if (getArguments().keySet().size() > 1) {
            view = inflater.inflate(R.layout.onboarding_story_fragment, container, false);
        }
        else {
            view = inflater.inflate(R.layout.onboarding_start, container, false);
        }

        ImageView dot1 = view.findViewById(R.id.dot1);
        ImageView dot2 = view.findViewById(R.id.dot2);
        ImageView dot3 = view.findViewById(R.id.dot3);
        ImageView dot4 = view.findViewById(R.id.dot4);
        ImageView dot5 = view.findViewById(R.id.dot5);
        int pos = getArguments().getInt("pos");
        dot1.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
        dot2.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
        dot3.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
        dot4.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
        dot5.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
        if (pos == 0) {
            dot1.setImageDrawable(getActivity().getDrawable(R.drawable.active));
        }
        else if (pos == 1) {
            dot2.setImageDrawable(getActivity().getDrawable(R.drawable.active));
        }
        else if (pos == 2) {
            dot3.setImageDrawable(getActivity().getDrawable(R.drawable.active));
        }
        else if (pos == 3) {
            dot4.setImageDrawable(getActivity().getDrawable(R.drawable.active));
        }
        else if (pos == 4) {
            dot5.setImageDrawable(getActivity().getDrawable(R.drawable.active));
        }

        if (getArguments().keySet().size() > 1) {
            ((OnboardingActivity) getActivity()).getSupportActionBar().hide();
            imageView = view.findViewById(R.id.storyImageView);
            contentView = view.findViewById(R.id.storyMessage);
            titleView = view.findViewById(R.id.storyTitle);

            imageView.setImageDrawable(getActivity().getDrawable(getArguments().getInt("image")));
            contentView.setText(getActivity().getString(getArguments().getInt("message")));
            titleView.setText(getActivity().getString(getArguments().getInt("title")));

        }
        else {

//            TextView privacy = (TextView) view.findViewById(R.id.privacyTextView);
//            TextView terms = (TextView) view.findViewById(R.id.termsTextView);
//            Utils.linkify(privacy, getString(R.string.privacy_link));
//            Utils.linkify(terms, getString(R.string.terms_link));

            Button privacy = (Button) view.findViewById(R.id.privacy);
            privacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = getString(R.string.covidSiteLink);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            Button getStartedButton = (Button) view.findViewById(R.id.onboardingGetStartedButton);
            getStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
                    tx.setCustomAnimations(
                            R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right,R.anim.exit_left_to_right);
//                    tx.setCustomAnimations(
//                            R.anim.enter_bottom_to_top,R.anim.no_anim,
//                            R.anim.enter_top_to_bottom,R.anim.no_anim);
//                    tx.addToBackStack(null);
                    tx.replace(R.id.fragment_container_onboarding, Constants.PermissionsFragment).commit();
                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.e("state", "story on resume");
    }
}
