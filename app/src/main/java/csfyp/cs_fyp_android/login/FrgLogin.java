package csfyp.cs_fyp_android.login;

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
 * Created by ray on 3/11/2016.
 */

public class FrgLogin extends CustomFragment{
    public FrgLogin(){
        super();
    }

    public final static String TAG = "login";
    private Toolbar mToolBar;

    public static FrgLogin newInstance() {

        Bundle args = new Bundle();

        FrgLogin fragment = new FrgLogin();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.login_frg,container,false);
        mToolBar = (Toolbar) v.findViewById(R.id.loginToolBar);
        mToolBar.setTitle("Login");
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
