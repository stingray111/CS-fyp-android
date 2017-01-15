package csfyp.cs_fyp_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.akexorcist.localizationactivity.LocalizationActivity;

import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.login.FrgLogin;

public class MainActivity extends LocalizationActivity {

    private FrgHome mHome;
    private String mToken;
    private int mUserId;
    private String mUsername;

    public void setmUserId(int userId){mUserId = userId;}

    public void setmToken(String token){mToken= token;}

    public void setmUsername(String username){mUsername= username;}

    public int getmUserId() {
        return mUserId;

    }

    public String getmToken() {
        return mToken;
    }

    public String getmUsername() {
        return mUsername;
    }

    public FrgHome getmHome() {
        return mHome;
    }

    public void setmHome(FrgHome mHome) {
        this.mHome = mHome;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FrgHome.HOME_LOCATION_SETTING_CALLBACK) {
            mHome.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        finish();
//        Intent refresh = new Intent(this, MainActivity.class);
//        startActivity(refresh);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()
//                .build());

        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mToken = sharedPref.getString("userToken", "");
        mUserId = sharedPref.getInt("userId", 0);
        mUsername = sharedPref.getString("username", "user");

        mHome = FrgHome.newInstance();

        if (!mToken.isEmpty()) {
            // user not login
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.parent_fragment_container, FrgLogin.newInstance());
            ft.commit();
        } else {

            // TODO: 6/11/2016 verify token 
            // user login

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.parent_fragment_container, mHome);
            ft.commit();
        }

    }
}
