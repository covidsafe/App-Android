package unused;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.corona.Constants;
import com.example.corona.MainActivity;
import com.example.corona.R;

public class HelpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme","HELP");
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.help_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("logme","HELP");
        Constants.HelpFragment = this;
        Constants.CurrentFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.HelpFragment = this;
        Constants.CurrentFragment = this;
    }
}
