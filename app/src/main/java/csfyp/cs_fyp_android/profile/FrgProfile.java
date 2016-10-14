package csfyp.cs_fyp_android.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.home.CustomFragment;

public class FrgProfile extends CustomFragment{
    public static final String TAG = "ProfileFragment";
    private Toolbar mToolBar;
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
        super.onCreateView(inflater, container, savedInstanceState);
        View v  = inflater.inflate(R.layout.profile_frg, container, false);
        mToolBar = (Toolbar) v.findViewById(R.id.profileToolBar);
        mToolBar.setTitle("username");
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });
        return v;
    }
}
