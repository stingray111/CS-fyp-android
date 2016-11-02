package csfyp.cs_fyp_android.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class FrgWelcome extends CustomFragment{
    public FrgWelcome(){
        super();
    }
    public final static String TAG = "welcome";

    public static FrgWelcome newInstance() {

        Bundle args = new Bundle();

        FrgWelcome fragment = new FrgWelcome();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.welcome_frg,container,false);
        return v;
    }
}
