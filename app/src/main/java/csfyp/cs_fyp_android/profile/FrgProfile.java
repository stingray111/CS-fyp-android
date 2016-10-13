package csfyp.cs_fyp_android.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.R;

public class FrgProfile extends Fragment{
    public FrgProfile() {
    }

    public static FrgProfile newInstance() {

        Bundle args = new Bundle();

        FrgProfile fragment = new FrgProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_frg, container, false);
    }
}
