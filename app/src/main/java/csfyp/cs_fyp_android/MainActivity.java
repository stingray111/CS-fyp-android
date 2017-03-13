package csfyp.cs_fyp_android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.util.List;

import csfyp.cs_fyp_android.chat.ChatService;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.SSL;
import csfyp.cs_fyp_android.lib.eventBus.ChatServiceSetting;
import csfyp.cs_fyp_android.lib.eventBus.ErrorMsg;
import csfyp.cs_fyp_android.login.FrgLogin;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.request.EventListRequest;
import csfyp.cs_fyp_android.model.respond.EventListRespond;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static csfyp.cs_fyp_android.login.FrgLogin.GOOGLE_SIGN_IN_CODE;

public class MainActivity extends LocalizationActivity {

    public FrgHome mHome;
    public static Location mCurrentLocation;
    private String mToken;
    private User mSelf;
    private int mUserId;
    public static String mUsername;
    private String mMsgToken;
    public ChatService mChatService;

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
        } else if (requestCode == GOOGLE_SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            EventBus.getDefault().post(result);
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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void errorToast(ErrorMsg err) {
        if (err.getDuration() == Toast.LENGTH_LONG){
            Toast.makeText(this, err.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, err.getErrorMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void chatServiceHandle(ChatServiceSetting chatServiceSetting){
        if (chatServiceSetting.getMode() == ChatServiceSetting.INIT) {
            HTTP httpService = HTTP.retrofit.create(HTTP.class);
            Call<EventListRespond> call = httpService.getEvents(new EventListRequest(mUserId, 3));
            call.enqueue(new Callback<EventListRespond>() {
                Response<EventListRespond> EventRespond;
                List<Event> eventList;
                String TAG = "ChatService(Activity)";

                @Override
                public void onResponse(Call<EventListRespond> call, Response<EventListRespond> response) {
                    if(response.isSuccessful() && response.body().getErrorMsg() == null){
                        eventList = response.body().getEvents();
                        Log.d(TAG,"here"+mMsgToken);
                        EventBus.getDefault().post(new ChatServiceSetting(ChatServiceSetting.SET_PARAM,eventList,mMsgToken));
                    }else{
                        EventBus.getDefault().post(new ErrorMsg("Server Error",ErrorMsg.LENGTH_SHORT));
                        Log.d(TAG,response.body().getErrorMsg());
                    }
                }

                @Override
                public void onFailure(Call<EventListRespond> call, Throwable t) {
                    EventBus.getDefault().post(new ErrorMsg("Cannot connect to messaging service, will try again",ErrorMsg.LENGTH_SHORT));
                    call.clone().enqueue(this);
                }
            });
        }
    }


}
