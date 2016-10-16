package csfyp.cs_fyp_android.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import csfyp.cs_fyp_android.R;


public class CustomFragment extends Fragment {
    public void switchFragment(Fragment fragment, String identifier) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.parent_fragment_container, fragment);
        ft.addToBackStack(identifier);
        ft.commit();
    }

    public void onBack(String identifier) {
        getActivity().onBackPressed();
    }
}
