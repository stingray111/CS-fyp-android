package csfyp.cs_fyp_android.chat;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import csfyp.cs_fyp_android.R;

import static android.view.View.GONE;
import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;


/**
 * Created by ray on 8/2/2017.
 */

public class ChatService extends Service {
    private String mMsgToken = "";
    private String TAG = "ChatService";
    public LocalBinder myBinder = new LocalBinder();
    private WindowManager mWindowManager;
    private View mView;
    private int mStatus;  //0: icon
    private int mWidth;//dp
    private int mHeight;//dp
    private int mXPos;//dp
    private int mYPos;//dp
    private WindowManager.LayoutParams mParams;
    private Handler mHandler;

    private FloatingActionMenu mFloatingActionMenu;
    private List<com.github.clans.fab.FloatingActionButton> mFloatingActionButtonList;
    private View mChatBox;
    private WindowManager.LayoutParams mParamsChatBox;
    private ProgressBar mProgressBar;
    private RecyclerView mMessageRecyclerView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //vars

    public class LocalBinder extends Binder {
        public ChatService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChatService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    Log.d(TAG, user.getUid());
                    startMsg();
                }
                else{
                    Log.d(TAG, "signedOut");
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView!= null) mWindowManager.removeView(mView);
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    public int dp2px(float dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public void setmMsgToken(String mMsgToken) {
        this.mMsgToken = mMsgToken;
    }

    public String getmMsgToken() {
        return mMsgToken;
    }





    public void startMsg(){

        mStatus= 0;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mView = li.inflate(R.layout.chat_float_frg,null);
        mChatBox = li.inflate(R.layout.chat_frame,null);

        mView.setOnDragListener(new View.OnDragListener(){
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (mStatus == 0 ){ // not expanded
                    //TODO: move the box
                }
                return true;
            }
        });

        mWidth = 150;
        mHeight = 150;

        mParams = new WindowManager.LayoutParams(
                dp2px(mWidth),
                dp2px(mHeight),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.LEFT| Gravity.TOP;

        final com.github.clans.fab.FloatingActionButton programFab1 = new com.github.clans.fab.FloatingActionButton(this);
        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab1.setLabelText("hey");
        programFab1.setImageResource(R.drawable.bg_event);
        programFab1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mChatBox.findViewById(R.id.chat_frame).setVisibility(View.VISIBLE);
                mStatus = 2;
            }
        });

        final com.github.clans.fab.FloatingActionButton programFab2 = new com.github.clans.fab.FloatingActionButton(this);
        programFab2.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab2.setLabelText("hey");
        programFab2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mChatBox.findViewById(R.id.chat_frame).setVisibility(View.VISIBLE);
                mStatus = 2;
            }
        });
        programFab2.setImageResource(R.drawable.bg_event);

        mFloatingActionButtonList = new LinkedList<com.github.clans.fab.FloatingActionButton>();
        mFloatingActionButtonList.add(programFab1);
        mFloatingActionButtonList.add(programFab2);


        //handling open the menu
        mFloatingActionMenu = (FloatingActionMenu) mView.findViewById(R.id.floatingMsgMenu);
        //Cannot use because not covering whole screen
        //mFloatingActionMenu.setClosedOnTouchOutside(true);
        mFloatingActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mStatus == 0) {

                    for (com.github.clans.fab.FloatingActionButton _fab : mFloatingActionButtonList) {
                        mFloatingActionMenu.addMenuButton(_fab);
                    }

                    mParams.width = WRAP_CONTENT;
                    mParams.height = WRAP_CONTENT;
                    mWindowManager.updateViewLayout(mView,mParams);

                    mStatus = 1;
                }
                else if(mStatus == 1) {
                    for (com.github.clans.fab.FloatingActionButton _fab : mFloatingActionButtonList) {
                        mFloatingActionMenu.removeMenuButton(_fab);
                    }
                    mParams.width = dp2px(mHeight);
                    mParams.height = dp2px(mWidth);
                    mWindowManager.updateViewLayout(mView,mParams);
                    mStatus = 0;
                }else if(mStatus == 2){
                    for (com.github.clans.fab.FloatingActionButton _fab : mFloatingActionButtonList) {
                        mFloatingActionMenu.removeMenuButton(_fab);
                    }
                    mParams.width = dp2px(mHeight);
                    mParams.height = dp2px(mWidth);
                    mWindowManager.updateViewLayout(mView,mParams);
                    mChatBox.findViewById(R.id.chat_frame).setVisibility(GONE);
                    mStatus = 0;
                }
                mFloatingActionMenu.toggle(true);
            }
        });
        mParams.x = dp2px(0);
        mParams.y = dp2px(50);
        mWindowManager.addView(mView, mParams);


        //ChatBox start
        mParamsChatBox = new WindowManager.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mChatBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatBox.setVisibility(GONE);
            }
        });
        mChatBox.findViewById(R.id.chat_inner).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });

        mProgressBar = (ProgressBar) mChatBox.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) mChatBox.findViewById(R.id.messageRecyclerView);
        //TODO: here


        mWindowManager.addView(mChatBox, mParamsChatBox);


    }

    public void login(){
        mAuth.signInWithCustomToken(mMsgToken)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                        Log.d("login firebase: ", "logining");
                        if(!task.isSuccessful()){
                            Log.d("login firebase: ", "login failed");
                        }
                    }
                });
    }

}
