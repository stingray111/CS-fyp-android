package csfyp.cs_fyp_android.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;

/**
 * Created by ray on 19/10/2016.
 */

public class FrgSetting extends CustomFragment{
    public FrgSetting(){super();}
    public static final String TAG = "Setting";
    private Toolbar mToolBar;

    public static FrgSetting newInstance() {
        Bundle args = new Bundle();
        FrgSetting fragment = new FrgSetting();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View v = inflater.inflate(R.layout.setting_frg,container,false);
        mToolBar = (Toolbar) v.findViewById(R.id.settingToolBar);
        mToolBar.setTitle("Setting");
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               onBack(TAG);
            }
        });

        return v;
    }
}
