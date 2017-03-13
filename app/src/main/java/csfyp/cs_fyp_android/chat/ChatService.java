package csfyp.cs_fyp_android.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.eventBus.ChatServiceSetting;
import csfyp.cs_fyp_android.lib.eventBus.ErrorMsg;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.request.EventListRequest;
import csfyp.cs_fyp_android.model.respond.EventListRespond;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;


/**
 * Created by ray on 8/2/2017.
 */

public class ChatService extends Service {
    private Point mWindowSize = new Point();
    private String mMsgToken;
    private String TAG = "ChatService";
    public LocalBinder myBinder = new LocalBinder();
    private WindowManager mWindowManager;
    private View mView;
    private int mStatus;  //0: icon
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

    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mFirebaseDatabaseReference;

    private FirebaseRecyclerAdapter<FriendlyMessage, RecyclerView.ViewHolder> mFirebaseAdapter;
    public static final String MESSAGES_CHILD = "testMsg";
    private static final String MESSAGE_URL = "https://cs-fyp.firebaseio.com/testMsg";
    private Button mSendButton;
    private EditText mMessageEditText;
    private List<Event> mEventList;
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
        EventBus.getDefault().post(new ChatServiceSetting(ChatServiceSetting.INIT));
        return myBinder;
    }


    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        super.onCreate();
        mHandler = new Handler();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null) {
                    Log.d(TAG, "Login:\t" + user.getUid());
                }
                else{
                    Log.d(TAG, "signedOut");
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        if (mView!= null) mWindowManager.removeView(mView);
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
        FirebaseAuth.getInstance().signOut();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void temp (){

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
        //put the event list in

        Log.d(TAG, "start messaging interface");

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
        mChatBox = li.inflate(R.layout.chat_frame,null);

        mParams = new WindowManager.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL  |WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        mParams.gravity = Gravity.LEFT| Gravity.TOP;

        mParams.x = dp2px(100);
        mParams.y = dp2px(100);

        mSendButton = (Button) mChatBox.findViewById(R.id.sendButton);
        mMessageEditText = (EditText) mChatBox.findViewById(R.id.messageEditText);

        mFloatingActionButtonList = new LinkedList<com.github.clans.fab.FloatingActionButton>();
        for(Event item:mEventList) {
            final com.github.clans.fab.FloatingActionButton btn = createButton(item);
            mFloatingActionButtonList.add(btn);
        }

        //handling open the menu
        mFloatingActionMenu = (FloatingActionMenu) mView.findViewById(R.id.floatingMsgMenu);
        for (com.github.clans.fab.FloatingActionButton _fab : mFloatingActionButtonList) {
            mFloatingActionMenu.addMenuButton(_fab);
        }

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
                        ? R.drawable.ic_speech_bubble: R.drawable.ic_close_black_24dp);
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

        //ChatBox start
        mParamsChatBox = new WindowManager.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
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

        mChatBox.findViewById(R.id.chatFrameBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatBox.setVisibility(GONE);
            }
        });

        mProgressBar = (ProgressBar) mChatBox.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) mChatBox.findViewById(R.id.messageRecyclerView);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mWindowManager.addView(mChatBox, mParamsChatBox);

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

    public boolean addEvent(Event e){
        final com.github.clans.fab.FloatingActionButton btn = createButton(e);
        mEventList.add(e);
        mFloatingActionButtonList.add(btn);
        mFloatingActionMenu.addMenuButton(btn);
        mFloatingActionMenu.setVisibility(View.VISIBLE);
        return true;
    }


    public boolean dropEvent(int eid){
        for(Event item:mEventList){
            if (item.getId() == eid){
                com.github.clans.fab.FloatingActionButton fab = mFloatingActionButtonList.get(mEventList.indexOf(item));
                mFloatingActionButtonList.remove(fab);
                mFloatingActionMenu.removeMenuButton(fab);
                mEventList.remove(item);
                if(mFloatingActionButtonList.size() == 0){
                    mFloatingActionMenu.setVisibility(GONE);
                }
                return true;
            }
        }
        return false;
    }

    private com.github.clans.fab.FloatingActionButton createButton(Event item){
        final com.github.clans.fab.FloatingActionButton btn = new com.github.clans.fab.FloatingActionButton(this);
        final String eventName = item.getName();
        final int eventId = item.getId();
        btn.setButtonSize(FloatingActionButton.SIZE_MINI);//TODO: change icon
        btn.setLabelText(item.getName());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FriendlyMessage friendlyMessage = new
                                FriendlyMessage(MainActivity.mUsername,
                                MainActivity.mUsername,
                                mMessageEditText.getText().toString());
                        mFirebaseDatabaseReference.child("messages/group_"+eventId)
                                .push().setValue(friendlyMessage);
                        mMessageEditText.setText("");
                    }
                });

                mChatBox.findViewById(R.id.chat_frame).setVisibility(View.VISIBLE);

                ((TextView)mChatBox.findViewById(R.id.chatFrameTitle)).setText(eventName);

                mProgressBar.setVisibility(ProgressBar.VISIBLE);

                mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, RecyclerView.ViewHolder>(
                        FriendlyMessage.class,
                        R.layout.item_message,
                        RecyclerView.ViewHolder.class,
                        mFirebaseDatabaseReference.child("messages/group_"+eventId)){
                    @Override
                    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, FriendlyMessage model, int position) {
                        int localType = getLocalType(model);
                        switch (localType){
                            case 0:
                                //others
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                ((MessageViewHolder)viewHolder).messageTextView.setText(model.getContent());
                                ((MessageViewHolder)viewHolder).messengerTextView.setText(model.getDisplayName());
                                ((MessageViewHolder) viewHolder).timeStamp.setText(model.getTime());
                                break;
                            case 10:
                                //own
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                ((MessageViewHolder)viewHolder).messageTextView.setText(model.getContent());
                                ((MessageViewHolder)viewHolder).timeStamp.setText(model.getTime());
                                break;
                        }
                    }

                    @Override
                    protected FriendlyMessage parseSnapshot(DataSnapshot snapshot){
                        FriendlyMessage friendlyMessage = super.parseSnapshot(snapshot);
                        if(friendlyMessage !=null ){
                            friendlyMessage.setId(snapshot.getKey());
                        }
                        return friendlyMessage;
                    }

                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        switch (viewType){
                            case 10:
                                View selfView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_message_own,parent,false);
                                return new MessageViewHolder(selfView);
                            case 0:
                                View otherView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_message,parent,false);
                                return new MessageViewHolder(otherView);
                        }
                        Log.d(TAG,"unhandled");
                        return null;
                    }


                    @Override
                    public int getItemViewType(int position) {
                        FriendlyMessage friendlyMessage = getItem(position);
                        return getLocalType(friendlyMessage);
                    }

                    public int getLocalType(FriendlyMessage friendlyMessage){
                        int localType = 0;
                        if(friendlyMessage.getUid().equals(MainActivity.mUsername)){
                            localType = friendlyMessage.getType()+10;
                        }
                        else{
                            localType = friendlyMessage.getType();
                        }
                        return localType;
                    }

                };

                mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                        int lastVisiblePosition =
                                mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                        // If the recycler view is initially being loaded or the
                        // user is at the bottom of the list, scroll to the bottom
                        // of the list to show the newly added message.
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            mMessageRecyclerView.scrollToPosition(positionStart);
                        }
                    }
                });

                if(mMessageRecyclerView.getAdapter() == null) {
                    mMessageRecyclerView.setAdapter(mFirebaseAdapter);
                }else{
                    mMessageRecyclerView.swapAdapter(mFirebaseAdapter,false);
                }
                mStatus = 2;
            }
        });
        btn.setImageResource(R.drawable.bg_event);
        return btn;
    }

    @Subscribe (threadMode = ThreadMode.ASYNC)
    public void loginWithContent(ChatServiceSetting chatServiceSetting){
        if(chatServiceSetting.getMode() == ChatServiceSetting.SET_PARAM) {
            mMsgToken = chatServiceSetting.getmMsgToken();
            mEventList = chatServiceSetting.getmEventList();
            Log.d(TAG,mMsgToken);
            mAuth.signInWithCustomToken(mMsgToken)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                EventBus.getDefault().post(new ErrorMsg("Cannot login to messaging service",ErrorMsg.LENGTH_LONG));
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startMsg();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }

}

