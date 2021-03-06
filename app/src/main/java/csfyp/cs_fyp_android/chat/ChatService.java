package csfyp.cs_fyp_android.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.Label;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.Utils;
import csfyp.cs_fyp_android.lib.eventBus.ChatFramePage;
import csfyp.cs_fyp_android.lib.eventBus.ChatServiceSetting;
import csfyp.cs_fyp_android.lib.eventBus.ErrorMsg;
import csfyp.cs_fyp_android.model.BitmapAndBtn;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.User;

import static android.view.View.GONE;
import static android.widget.ListPopupWindow.WRAP_CONTENT;


/**
 * Created by ray on 8/2/2017.
 */

public class ChatService extends Service {
    private Point mWindowSize = new Point();
    private String TAG = "ChatService";
    public LocalBinder myBinder = new LocalBinder();
    private WindowManager mWindowManager;
    private View mView;
    private int mStatus;
    private WindowManager.LayoutParams mParams;
    private Handler mHandler;
    private User mSelf;
    private String selfStr;
    private volatile int showingEventId;
    private volatile String showingEventName;
    public static volatile boolean active = false;
    public static volatile boolean interfaceStarted =false;

    private FloatingActionMenu mFloatingActionMenu;
    private List<com.github.clans.fab.FloatingActionButton> mFloatingActionButtonList = new ArrayList<com.github.clans.fab.FloatingActionButton>();

    private FirebaseAuth mAuth;

    private FirebaseRecyclerAdapter<FriendlyMessage, RecyclerView.ViewHolder> mFirebaseAdapter;
    public static List<Event> mEventList = new ArrayList<Event>();
    private ProPicManager proPicManager = new ProPicManager();

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
        active = true;
        Log.d(TAG,"onCreate");
        super.onCreate();
        mHandler = new Handler();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mWindowManager== null || mView == null)
            startMsg();
        active = true;
        Log.d(TAG,"onStartCommand");

        boolean fromHome = intent.getBooleanExtra("fromHome",false);
        this.selfStr = intent.getStringExtra("self");
        Gson gson = new Gson();
        mSelf = gson.fromJson(selfStr,User.class);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        active = false;
        Log.d(TAG,"onDestroy");
        EventBus.getDefault().unregister(this);
        if (mView!= null && mWindowManager!=null) mWindowManager.removeView(mView);
        mView = null;
        mWindowManager = null;
        super.onDestroy();
    }

    public void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }


    public void startMsg(){
        //put the event list in

        Log.d(TAG, "start messaging interface");
        interfaceStarted = true;

        mStatus= 0;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getSize(mWindowSize);
        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mView = li.inflate(R.layout.chat_float_frg,null);
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mParams = new WindowManager.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL  |WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.LEFT| Gravity.TOP;

        mParams.x = Utils.dpToPx(getBaseContext(),100);
        mParams.y = Utils.dpToPx(getBaseContext(),100);

        /*
        for(Event item:mEventList) {
            final com.github.clans.fab.FloatingActionButton btn = createButton(item);
            mFloatingActionButtonList.add(btn);
        }

        for (com.github.clans.fab.FloatingActionButton _fab : mFloatingActionButtonList) {
            mFloatingActionMenu.addMenuButton(_fab);
            mFloatingActionMenu.setVisibility(View.VISIBLE);
        }
        */

        mFloatingActionButtonList = new LinkedList<com.github.clans.fab.FloatingActionButton>();
        mFloatingActionMenu = (FloatingActionMenu) mView.findViewById(R.id.floatingMsgMenu);
        mFloatingActionMenu.setVisibility(View.GONE);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mFloatingActionMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mFloatingActionMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mFloatingActionMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mFloatingActionMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFloatingActionMenu.getMenuIconView().setImageResource(mFloatingActionMenu.isOpened()
                        ? R.drawable.ic_app_icon_for_chat: R.drawable.ic_close_black_24dp);
            }
        });
        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        mFloatingActionMenu.setIconToggleAnimatorSet(set);

        mView.findViewById(R.id.floatingMsgMenuForeground).setOnTouchListener(new View.OnTouchListener() {
            private int CLICK_ACTION_THRESHHOLD = 150;
            private float startX;
            private float startY;
            private float preX;
            private float preY;
            boolean isAClick = true;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float endX = event.getRawX();
                float endY = event.getRawY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        isAClick = true;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (isClick(startX, endX, startY, endY) && isAClick) {
                            validLocation();
                            clickEvent();
                            return false;
                        }
                        else {
                            move((int)(endX-preX),(int)(endY-preY));
                            validLocation();
                            return false;
                        }

                    case MotionEvent.ACTION_MOVE:
                        if(!isClick(startX,endX,startY,endY)) {
                            isAClick = false;
                        }
                        move((int)(endX-preX),(int)(endY-preY));
                        break;

                }
                preX = event.getRawX();
                preY = event.getRawY();
                return true;
            }

            private boolean isClick(float startX, float endX, float startY, float endY) {
                float differenceX = Math.abs(startX - endX);
                float differenceY = Math.abs(startY - endY);
                if (differenceX > CLICK_ACTION_THRESHHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHHOLD) {
                    return false;
                }
                return true;
            }

            private void move(int diffx, int diffy){
                mParams.x = mParams.x + diffx;
                mParams.y = mParams.y + diffy;

                mWindowManager.updateViewLayout(mView, mParams);
            }

            private void validLocation(){
                int fabSize = getResources().getDimensionPixelSize(R.dimen.fab_size_normal);
                if(mParams.x < 0){
                    mParams.x = 0;
                }
                if(mParams.y < 0){
                    mParams.y = 0;
                }

                if(mParams.y + fabSize*2 > mWindowSize.y){
                    mParams.y = mWindowSize.y - fabSize*2;
                }

                if(mParams.x + fabSize > mWindowSize.x){
                    mParams.x = mWindowSize.x - fabSize;
                }
                mWindowManager.updateViewLayout(mView,mParams);
            }
        });

        mWindowManager.addView(mView, mParams);

    }


    public List<Event> getmEventList() {
        return mEventList;
    }

    private void clickEvent(){
        if(mStatus == 0) {
            mFloatingActionMenu.toggle(true);
            mStatus = 1;
        }
        else if(mStatus > 0 ) {
            mFloatingActionMenu.toggle(true);
            mStatus = 0;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addEvent(ChatServiceSetting css){
        if(css.getMode() == ChatServiceSetting.ADD_EVENT) {
            Event e = css.getEventObj();
            final com.github.clans.fab.FloatingActionButton btn = createButton(e);
            mEventList.add(e);
            mFloatingActionButtonList.add(btn);
            mFloatingActionMenu.addMenuButton(btn);
            mFloatingActionMenu.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dropEvent(ChatServiceSetting css){
        if(css.getMode() == ChatServiceSetting.REMOVE_EVENT) {
            int eid = css.getRmEventId();
            for (Event item : mEventList) {
                if (item.getId() == eid) {
                    com.github.clans.fab.FloatingActionButton fab = mFloatingActionButtonList.get(mEventList.indexOf(item));
                    mFloatingActionButtonList.remove(fab);
                    mFloatingActionMenu.removeMenuButton(fab);
                    mEventList.remove(item);
                    if (mFloatingActionButtonList.size() == 0) {
                        mFloatingActionMenu.setVisibility(GONE);
                    }
                    return;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void swapEventList(ChatServiceSetting css){
        if(css.getMode() == ChatServiceSetting.SWAP_EVENT_LIST){
            mFloatingActionButtonList.clear();
            mEventList.clear();
            mFloatingActionMenu.removeAllMenuButtons();

            for(Event event:css.getmEventList()){
                final com.github.clans.fab.FloatingActionButton btn = createButton(event);
                mEventList.add(event);
                mFloatingActionButtonList.add(btn);
                mFloatingActionMenu.addMenuButton(btn);
            }
            if(mEventList.size() == 0){
                mFloatingActionMenu.setVisibility(GONE);
            }else{
                mFloatingActionMenu.setVisibility(View.VISIBLE);
            }
        }
    }

    private com.github.clans.fab.FloatingActionButton createButton(Event item){
        final com.github.clans.fab.FloatingActionButton btn = new com.github.clans.fab.FloatingActionButton(this);
        final String eventName = item.getName();
        final int eventId = item.getId();
        btn.setButtonSize(FloatingActionButton.SIZE_MINI);

        addPictureToButotn(item.getHolder().getProPic(),btn);

        btn.setLabelText(item.getName());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatService.this.showingEventId = eventId;
                ChatService.this.showingEventName = eventName;

                mFloatingActionMenu.close(true);
                if(!ChatFrameActivity.active) {
                    Intent it;
                    it = new Intent(ChatService.this, ChatFrameActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.putExtra("eventId", eventId);
                    it.putExtra("mSelf", selfStr);
                    it.putExtra("eventName", eventName);
                    PendingIntent pit = PendingIntent.getActivity(ChatService.this, 0, it, 0);
                    try {
                        pit.send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    EventBus.getDefault().post(new ChatFramePage(mSelf, eventId, eventName));
                }
            }
        });
        return btn;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setBtnImage(BitmapAndBtn btb){
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getBaseContext().getResources(),btb.bitmap);
        if(btb.bitmap == null){
            Log.d(TAG,"is null");
        }
        roundedBitmapDrawable.setCircular(true);
        btb.btn.setImageDrawable(roundedBitmapDrawable);
    }

    public void addPictureToButotn(final String url, com.github.clans.fab.FloatingActionButton btn) {
        try {
            proPicManager.setBtn(url, getBaseContext(), btn);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void requestChatFrameUpdate(ChatFramePage chatFramePage){
        if(chatFramePage.mode == ChatFramePage.REQUEST){
            Log.d(TAG,"return the values");
            EventBus.getDefault().post(new ChatFramePage(mSelf,showingEventId,showingEventName));
        }
    }



}

