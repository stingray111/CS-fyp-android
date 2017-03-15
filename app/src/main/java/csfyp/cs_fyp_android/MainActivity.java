package csfyp.cs_fyp_android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.google.gson.Gson;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;

import csfyp.cs_fyp_android.chat.ChatService;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.lib.SSL;
import csfyp.cs_fyp_android.lib.eventBus.PropicUpdate;
import csfyp.cs_fyp_android.login.FrgLogin;
import csfyp.cs_fyp_android.model.User;

public class MainActivity extends LocalizationActivity {

    public FrgHome mHome;
    public static Location mCurrentLocation;
    private String mToken;
    private User mSelf;
    private int mUserId;
    public static String mUsername;
    private String mMsgToken;
    private boolean mIsBound = false;
    public ChatService mChatService;

    public void setmIsBound(boolean mIsBound) {
        this.mIsBound = mIsBound;
    }

    public boolean getmIsBound(){
        return this.mIsBound;
    }

    public void setmMsgToken(String mMsgToken) {
        this.mMsgToken = mMsgToken;
    }

    public User getmSelf() {
        return mSelf;
    }

    public void setmSelf(User mSelf) {
        this.mSelf = mSelf;
    }

    public void setmUserId(int userId) {
        mUserId = userId;
    }

    public void setmToken(String token) {
        mToken = token;
    }

    public void setmUsername(String username) {
        mUsername = username;
    }

    public int getmUserId() {
        return mUserId;

    }

    public String getmMsgToken() {
        return mMsgToken;
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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FrgHome.HOME_LOCATION_SETTING_CALLBACK:
                mHome.onActivityResult(requestCode, resultCode, data);
                break;
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), this, true, 1, 1, 0, 0);
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, this);
                    Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), this, true, 1, 1, 0, 0);
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri i = Uri.fromFile(CroperinoFileUtil.getmFileTemp());
                    //Log.i("Propic", i.toString());
                    EventBus.getDefault().postSticky(new PropicUpdate(CroperinoFileUtil.getmFileTemp()));
                }
                break;
            default:
                break;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        if (mSelf == null) {
            SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
            Gson gson = new Gson();
            String self = mPrefs.getString("self", "");
            mSelf = gson.fromJson(self, User.class);
        }

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
        mMsgToken = sharedPref.getString("msgToken", null);

        mHome = FrgHome.newInstance();

        boolean debugMode = false;

        InputStream is = (InputStream) this.getResources().openRawResource(R.raw.server);
        try {
            SSL.setServerCert(is);
        } catch (java.io.IOException e) {
            Toast.makeText(this, "SSL Error: please restart the app", Toast.LENGTH_LONG).show();
        }

        if (debugMode) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.parent_fragment_container, FrgRating.newInstance(mUserId, "ken31ee"));
//            ft.commit();

        } else {
            if (mToken.isEmpty()) {
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

    public ServiceConnection connection = new ServiceConnection() {
        // 成功與 Service 建立連線
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("main", "MainActivity onServiceConnected");
            mChatService = (((ChatService.LocalBinder)service).getService());
            //mChatService.startMsg();
            mIsBound = true;
        }
        // 與 Service 建立連線失敗
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("main", "MainActivity onServiceFailed");
            mChatService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
