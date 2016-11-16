package csfyp.cs_fyp_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import csfyp.cs_fyp_android.databinding.ActivityMainBinding;
import csfyp.cs_fyp_android.event.FrgEvent;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.login.FrgLogin;

public class MainActivity extends AppCompatActivity {

    private FrgHome mHome;
    private String mToken;
    private int mUserId;

    public int getmUserId() {
        return mUserId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FrgHome.HOME_LOCATION_SETTING_CALLBACK){
            mHome.onActivityResult(requestCode, resultCode, data);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .build());

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mToken = sharedPref.getString("userToken", "");
        mUserId = sharedPref.getInt("userId", 0);

        if (mToken.isEmpty()) {
            // user not login
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.parent_fragment_container, FrgHome.newInstance());
            ft.commit();
        } else {

            // TODO: 6/11/2016 verify token 
            // user login
            mHome = FrgHome.newInstance();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.parent_fragment_container, mHome);
            ft.commit();
        }


    }
}
