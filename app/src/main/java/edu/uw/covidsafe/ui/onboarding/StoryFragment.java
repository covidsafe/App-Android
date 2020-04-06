package edu.uw.covidsafe.ui.onboarding;

import android.annotation.SuppressLint;
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

import com.example.covidsafe.R;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class StoryFragment extends Fragment {

    ImageView imageView;
    TextView tview;

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
            ((OnboardingActivity) getActivity()).getSupportActionBar().hide();
            imageView = view.findViewById(R.id.storyImageView);
            tview = view.findViewById(R.id.storyMessage);

            imageView.setImageDrawable(getActivity().getDrawable(getArguments().getInt("image")));
            tview.setText(getActivity().getString(getArguments().getInt("message")));

            ImageView dot1 = view.findViewById(R.id.dot1);
            ImageView dot2 = view.findViewById(R.id.dot2);
            ImageView dot3 = view.findViewById(R.id.dot3);
            ImageView dot4 = view.findViewById(R.id.dot4);
            int pos = getArguments().getInt("pos");
            dot1.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
            dot2.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
            dot3.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
            dot4.setImageDrawable(getActivity().getDrawable(R.drawable.deactive));
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
        }
        else {
            view = inflater.inflate(R.layout.onboarding_start, container, false);

            TextView privacy = (TextView) view.findViewById(R.id.privacyTextView);
            TextView terms = (TextView) view.findViewById(R.id.termsTextView);
            Utils.linkify(privacy, getString(R.string.privacy_link));
            Utils.linkify(terms, getString(R.string.terms_link));

            Button getStartedButton = (Button) view.findViewById(R.id.onboardingGetStartedButton);
            getStartedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(
                            R.id.fragment_container_onboarding, Constants.PermissionsFragment).commit();
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
