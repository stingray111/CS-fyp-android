package csfyp.cs_fyp_android.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.R;

public class FrgExpandPanelAppBar extends Fragment {

    public FrgExpandPanelAppBar() {
    }

    public static FrgExpandPanelAppBar newInstance() {

        Bundle args = new Bundle();

        FrgExpandPanelAppBar fragment = new FrgExpandPanelAppBar();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.expand_panel_appbar_frg, container, false);
    }
}
