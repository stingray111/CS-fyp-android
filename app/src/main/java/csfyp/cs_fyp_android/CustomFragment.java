package csfyp.cs_fyp_android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


public class CustomFragment extends Fragment {
    public void switchFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_bottom_exit, R.anim.frg_slide_bottom_enter, R.anim.frg_slide_top_exit)
                .replace(R.id.parent_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void onBack(String identifier) {
        getActivity().onBackPressed();
    }
}
