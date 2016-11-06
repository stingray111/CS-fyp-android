package csfyp.cs_fyp_android.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    private Button loginBtn;
    private Button registerBtn;

    public static FrgWelcome newInstance() {
        Bundle args = new Bundle();
        FrgWelcome fragment = new FrgWelcome();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.welcome_frg,container,false);
        super.onCreateView(inflater, container, savedInstanceState);
        loginBtn = (Button) v.findViewById(R.id.welcomeLoginBtn);
        loginBtn.setOnClickListener(new Button.OnClickListener (){
            @Override
            public void onClick(View v) {
                switchFragment(getTargetFragment(), FrgLogin.newInstance());
            }
        });
        registerBtn = (Button) v.findViewById(R.id.welcomeLoginBtn);
        //registerBtn.setOnClickListener(switchFragment(FrgReg);
        return v;
    }
}
