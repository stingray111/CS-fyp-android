package csfyp.cs_fyp_android;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;

import com.akexorcist.localizationactivity.LocalizationActivity;

import java.io.InputStream;

import csfyp.cs_fyp_android.chat.ChatService;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.login.FrgLogin;
import csfyp.cs_fyp_android.lib.SSL;

import android.util.Log;
import android.widget.Toast;
public class MainActivity extends LocalizationActivity {

    private FrgHome mHome;
    private String mToken;
    private int mUserId;
    private String mUsername;
    private String mMsgToken;
    private ChatService mChatService;

    public void setmChatService(ChatService chatService) {
        this.mChatService = chatService;
    }

    public ChatService getmChatService() {
        return mChatService;
    }

    public void setmMsgToken(String mMsgToken) {
        this.mMsgToken = mMsgToken;
    }

    public void setmUserId(int userId){mUserId = userId;}

    public void setmToken(String token){mToken= token;}

    public void setmUsername(String username){mUsername= username;}

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
        mMsgToken = sharedPref.getString("msgToken", "");

        mHome = FrgHome.newInstance();

        Log.d("act","fuck first");
        //Intent serviceIntent = new Intent(getApplicationContext(), ChatService.class);
        //bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        //startService(serviceIntent);
        startService(new Intent(getApplicationContext(),ChatService.class));
        Log.d("act","fuck last");

        InputStream is = (InputStream) this.getResources().openRawResource(R.raw.server);
        try {
            SSL.setServerCert(is);
        }catch (java.io.IOException e){
            Toast.makeText(this,"SSL Error: please restart the app", Toast.LENGTH_LONG).show();
        }

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

    /*
    public ServiceConnection connection = new ServiceConnection() {
        // 成功與 Service 建立連線
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            setmChatService(((ChatService.LocalBinder)service).getService());
            Log.d("main", "MainActivity onServiceConnected");
        }
        // 與 Service 建立連線失敗
        @Override
        public void onServiceDisconnected(ComponentName name) {
            setmChatService(null);
            Log.d("main", "MainActivity onServiceFailed");
        }
    };
    */
}
