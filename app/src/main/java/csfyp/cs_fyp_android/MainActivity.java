package csfyp.cs_fyp_android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import csfyp.cs_fyp_android.chat.ChatFrameActivity;
import csfyp.cs_fyp_android.chat.ChatService;
import csfyp.cs_fyp_android.chat.FriendlyMessage;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.lib.EnqueueAgain;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.SSL;
import csfyp.cs_fyp_android.lib.Utils;
import csfyp.cs_fyp_android.lib.eventBus.ChatFramePage;
import csfyp.cs_fyp_android.lib.eventBus.PropicUpdate;
import csfyp.cs_fyp_android.lib.eventBus.ChatServiceSetting;
import csfyp.cs_fyp_android.lib.eventBus.ErrorMsg;
import csfyp.cs_fyp_android.lib.eventBus.PropicUpdate;
import csfyp.cs_fyp_android.lib.eventBus.SnackBarMessageContent;
import csfyp.cs_fyp_android.lib.eventBus.Toggle;
import csfyp.cs_fyp_android.login.FrgLogin;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.request.EventListRequest;
import csfyp.cs_fyp_android.model.request.UserName;
import csfyp.cs_fyp_android.model.respond.EventListRespond;
import csfyp.cs_fyp_android.model.respond.MsgTokenUpdateRespond;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.design.widget.Snackbar.make;
import static org.greenrobot.eventbus.ThreadMode.ASYNC;
import static org.greenrobot.eventbus.ThreadMode.MAIN;

public class MainActivity extends LocalizationActivity {

    private static final int PERMISSIONS_MULTIPLE_REQUEST = 1324;
    private static final String permissionString[] = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.INTERNET};
    private static String TAG = "MainActiivty";
    public FrgHome mHome;
    public static Location mCurrentLocation;
    private boolean mIsPermissionGranted = false;
    private String mToken;
    private User mSelf;
    private int mUserId;
    public static String mUsername;
    private String mMsgToken;
    private int mAcType;
    public static boolean active;
    private static final int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 9348;

    //var for messaging
    public List<Event> messageFullEventList;
    public FirebaseAuth mAuth;
    public volatile boolean floatingChatMenuOn = true;
    public volatile boolean floatingChatMenuAuto = true;
    public List<Event> floatingChatMenuShowing;

    public void setmAcType(int mAcType) {
        this.mAcType = mAcType;
    }

    public int getmAcType() {
        return mAcType;
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

    public String getmToken() {
        return mToken;
    }

    public String getmUsername() {
        return mUsername;
    }

    public FrgHome getmHome() {
        return mHome;
    }

    public boolean ismIsPermissionGranted() {
        return mIsPermissionGranted;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET) ) {

                make(findViewById(R.id.parent_fragment_container), "Please Grant Permissions", Snackbar.LENGTH_INDEFINITE)
                        .setAction("ENABLE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionString, PERMISSIONS_MULTIPLE_REQUEST);
                                }
                            }
                        }).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissionString, PERMISSIONS_MULTIPLE_REQUEST);
                }
            }
        } else {
            mIsPermissionGranted = true;

        }
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
            case OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG:
                if (!Utils.canDrawOverlays(MainActivity.this)) {
                    MainActivity.this.floatingChatMenuOn = false;
                }else {
                    chatHeadInit();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isPermissionGranted", mIsPermissionGranted);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            mIsPermissionGranted = savedInstanceState.getBoolean("isPermissionGranted");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !mIsPermissionGranted) {
            checkPermission();
        } else {
            mIsPermissionGranted = true;
        }

        active = true;

        Log.d(TAG,"main create intent"+getIntent().toString());

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch(Exception e){
        }
        if (mSelf == null) {
            SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
            Gson gson = new Gson();
            String self = mPrefs.getString("self", "");
            mSelf = gson.fromJson(self, User.class);
        }

        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mToken = sharedPref.getString("userToken", "");
        mUserId = sharedPref.getInt("userId", 0);
        mUsername = sharedPref.getString("username", "user");
        mMsgToken = sharedPref.getString("msgToken", null);
        mAcType = sharedPref.getInt("acType",0);


        boolean debugMode = false;

        InputStream is = (InputStream) this.getResources().openRawResource(R.raw.server);
        try {
            SSL.setServerCert(is);
        } catch (java.io.IOException e) {
            Toast.makeText(this, "SSL Error: please restart the app", Toast.LENGTH_LONG).show();
        }

        if (mToken.isEmpty()) {
            // user not login
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.parent_fragment_container, FrgLogin.newInstance());
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean fineLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFilePermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFilePermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean alertWindowPermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean internetPermission = grantResults[6] == PackageManager.PERMISSION_GRANTED;


                    if(fineLocationPermission && coarseLocationPermission && cameraPermission && readExternalFilePermission && writeExternalFilePermission && alertWindowPermission && internetPermission)
                    {
                        mIsPermissionGranted = true;
                    }
                }
                else {
                    Snackbar sb = Snackbar.make(this.findViewById(android.R.id.content), "Please Grant Permissions", Snackbar.LENGTH_INDEFINITE)
                            .setAction("ENABLE", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionString, PERMISSIONS_MULTIPLE_REQUEST);
                                    }
                                }
                            });
                    sb.setActionTextColor(ContextCompat.getColor(this,R.color.white)).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        active = false;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        active = true;
        super.onStart();
        Log.d("Intent", "start:"+Utils.intentToString(getIntent()));

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        active = false;
        super.onStop();
    }

    @Subscribe(threadMode = MAIN)
    public void errorToast(ErrorMsg err) {
        if (err.getDuration() == Toast.LENGTH_LONG){
            Toast.makeText(this, err.getErrorMsg(), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, err.getErrorMsg(), Toast.LENGTH_SHORT).show();
        }
    }
    @Subscribe(threadMode = MAIN)
    public void snackBarMessage(SnackBarMessageContent snackBarMessageContent){
        final Snackbar snackBar = make(findViewById(R.id.parent_fragment_container),snackBarMessageContent.message,Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(ContextCompat.getColor(this,R.color.white));
        if(snackBarMessageContent.action == null)
            snackBar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackBar.dismiss();
                }
            });
        else
            snackBar.setAction(snackBarMessageContent.action, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo reconnect here
                    snackBar.dismiss();
                }
            });
        snackBar.show();
    }

    @Subscribe(threadMode = MAIN)
    public void ToggleUniversalProgressBar(Toggle toggle) {
        if (toggle.getMode() == Toggle.SHOW) {
            findViewById(R.id.universalProgressBar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.universalProgressBar).setVisibility(View.GONE);
        }
    }


    @Subscribe(threadMode = MAIN)
    public void setmHome(FrgHome mHome) {
        this.mHome = mHome;
    }

    @Subscribe(threadMode = MAIN)
    public void startChatFrame(final ChatServiceSetting css){
        if(css.getMode() == ChatServiceSetting.CallChatFrame){
            Gson gson = new Gson();
            String selfStr = gson.toJson(mSelf);
            Intent it;
            it = new Intent(MainActivity.this,ChatFrameActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            it.putExtra("eventId", css.getEventObj().getId());
            it.putExtra("mSelf", selfStr);
            it.putExtra("eventName", css.getEventObj().getName());
            PendingIntent pit = PendingIntent.getActivity(MainActivity.this,0,it,0);
            try{
                pit.send();
            }catch (Exception e){
                e.printStackTrace();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new ChatFramePage(mSelf,css.getEventObj().getId(),css.getEventObj().getName()));
                }
            },1000);
        }
    }


    public void chatAuthInit(){
        mAuth = FirebaseAuth.getInstance();
        HTTP httpService = HTTP.retrofit.create(HTTP.class);
        Call<EventListRespond> call = httpService.getEvents(new EventListRequest(mUserId, 3));
        call.enqueue(new Callback<EventListRespond>() {
            Response<EventListRespond> EventRespond;
            List<Event> eventList;
            String TAG = "ChatService(Activity)";

            @Override
            public void onResponse(Call<EventListRespond> call, Response<EventListRespond> response) {
                if (response.isSuccessful() && response.body().getErrorMsg() == null) {
                    eventList = response.body().getEvents();
                    sortEventsByDate(eventList);
                    MainActivity.this.messageFullEventList = eventList;
                    Log.d(TAG, "token: " + mMsgToken);
                    //EventBus.getDefault().post(new ChatServiceSetting(ChatServiceSetting.SET_PARAM, eventList, mSelf));

                    for(Event event: eventList){
                        FirebaseMessaging.getInstance().subscribeToTopic("group_"+event.getId());
                    }

                    Log.d(TAG,"firebase login trying");
                    mAuth.signInWithCustomToken(mSelf.getMsgToken())
                            .addOnCompleteListener(new FirebaseAuthOnComplete())
                            .addOnFailureListener(new FirebaseAuthOnFailure());
                } else {
                    EventBus.getDefault().post(new ErrorMsg("Server Error", ErrorMsg.LENGTH_SHORT));
                    if (response.isSuccessful()) Log.d(TAG, response.body().getErrorMsg());
                }
            }

            @Override
            public void onFailure(final Call<EventListRespond> call, Throwable t) {
                EventBus.getDefault().post(new ErrorMsg("Cannot connect to messaging service, will try again", ErrorMsg.LENGTH_LONG));
                new Handler().postDelayed(
                        new EnqueueAgain<EventListRespond>(call, this)
                        , 5000);
            }
        });
    }

    public class FirebaseAuthOnFailure implements OnFailureListener{
        public void onFailure(@NonNull Exception e) {
            if(e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                HTTP httpservice = HTTP.retrofit.create(HTTP.class);
                Call<MsgTokenUpdateRespond> call = httpservice.msgTokenUpdate(new UserName(mUsername));
                call.enqueue(new Callback<MsgTokenUpdateRespond>() {
                    String TAG = "ChatService(Activity)";
                    @Override
                    public void onResponse(Call<MsgTokenUpdateRespond> call, Response<MsgTokenUpdateRespond> response) {
                        if(response.isSuccessful() && response.body().isSuccessful()){
                            mMsgToken = response.body().getMsgToken();
                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("msgToken", response.body().getMsgToken());

                            Gson gson = new Gson();
                            mSelf.setMsgToken(mMsgToken);
                            String selfStr = gson.toJson(mSelf);
                            editor.putString("self", selfStr);
                            editor.commit();

                            mAuth.signInWithCustomToken(mSelf.getMsgToken())
                                    .addOnCompleteListener(new FirebaseAuthOnComplete())
                                    .addOnFailureListener(new FirebaseAuthOnFailure());
                        }else{
                            EventBus.getDefault().post(new ErrorMsg("Message Server Error",Toast.LENGTH_LONG));
                        }
                    }

                    @Override
                    public void onFailure(Call<MsgTokenUpdateRespond> call, Throwable t) {
                        Log.d(TAG, "failed for response");
                        new Handler().postDelayed(
                                new EnqueueAgain<MsgTokenUpdateRespond>(call, this)
                                , 5000
                        );
                    }
                });


            }else {
                EventBus.getDefault().post(new ErrorMsg("Cannot login to messaging service", ErrorMsg.LENGTH_LONG));
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                mAuth.signInWithCustomToken(mSelf.getMsgToken())
                                        .addOnCompleteListener(new FirebaseAuthOnComplete())
                                        .addOnFailureListener(new FirebaseAuthOnFailure());
                            }
                        },
                        5000
                );   //TODO: update token
            }
        }
    }

    public class FirebaseAuthOnComplete implements OnCompleteListener {
        @Override
        public void onComplete(@NonNull Task task) {
            if(task.isSuccessful()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "IID_Token: " + FirebaseInstanceId.getInstance().getToken());
                        if(MainActivity.this.floatingChatMenuOn){
                            //the chat head is on
                            if (!Utils.canDrawOverlays(MainActivity.this)) {
                                Log.d("HERE", "request permission");
                                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG);
                            }
                            else {
                                chatHeadInit();
                                if (MainActivity.this.floatingChatMenuAuto) {
                                    //AUTO mode
                                    setFloatingChatToAuto();
                                } else {
                                    //get the custom list
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    public void setFloatingChatToAuto(){
        if (MainActivity.this.messageFullEventList.size() <= 2) {
            MainActivity.this.floatingChatMenuShowing = MainActivity.this.messageFullEventList;
        } else {
            MainActivity.this.floatingChatMenuShowing = MainActivity.this.messageFullEventList.subList(0, 3);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new ChatServiceSetting(floatingChatMenuShowing, ChatServiceSetting.SWAP_EVENT_LIST));
            }
        }, 1000);

    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "main new intent"+ intent.toString());
        setIntent(intent);
    }

    public List<Event> getMessageFullEventList() {
        return messageFullEventList;
    }


    public void addEventChat(Event event){
        FirebaseMessaging.getInstance().subscribeToTopic("group_"+event.getId());
        messageFullEventList.add(event);
        sortEventsByDate(messageFullEventList);

        if(floatingChatMenuOn && floatingChatMenuAuto){
            List<Event> newList;
            if(messageFullEventList.size()<=3){
                newList = messageFullEventList;
            }
            else {
                newList = messageFullEventList.subList(0, 3);
            }
            EventBus.getDefault().post(new ChatServiceSetting(newList, ChatServiceSetting.SWAP_EVENT_LIST));
            floatingChatMenuShowing = newList;
        }
    }

    public void removeEventChat(int eventId){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("group_"+eventId);
        for(Event event:messageFullEventList){
            if(event.getId() == eventId){
                messageFullEventList.remove(event);
                break;
            }
        }
        if(floatingChatMenuOn) {
            if (floatingChatMenuAuto) {
                List<Event> newList;
                if (messageFullEventList.size() <= 3) {
                    newList = messageFullEventList;
                } else {
                    newList = messageFullEventList.subList(0, 3);
                }
                EventBus.getDefault().post(new ChatServiceSetting(newList, ChatServiceSetting.SWAP_EVENT_LIST));
                floatingChatMenuShowing = newList;
            } else {
                //TODO
            }
        }
    }


    public void sortEventsByDate(List<Event> events){
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                try {
                    Date o1_time = o1.getStartTimeInDate();
                    Date o2_time = o2.getStartTimeInDate();
                    return o1_time.compareTo(o2_time);
                }catch(Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        messageFullEventList = events;
    }

    public void chatHeadInit(){
        Intent serviceIntent = new Intent(this, ChatService.class);
        serviceIntent.putExtra("fromHome",true);
        Gson gson = new Gson();
        String selfStr = gson.toJson(mSelf);
        serviceIntent.putExtra("self",selfStr);
        startService(serviceIntent);
    }


    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }


    private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}

