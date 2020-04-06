package unused;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

import com.example.covidsafe.R;

public class SymptomTrackerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_symptom_tracker, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.health_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","symptom tracker fragment onresume");

        Constants.CurrentFragment = this;
        Constants.SymptomTrackerFragment = this;

        RecyclerView view = getActivity().findViewById(R.id.recyclerView);

        SymptomRecyclerViewAdapter adapter = new SymptomRecyclerViewAdapter(getActivity(), getActivity());
        view.setAdapter(adapter);
        view.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.SymptomTrackerFragment = this;
        Constants.CurrentFragment = this;
    }
}
