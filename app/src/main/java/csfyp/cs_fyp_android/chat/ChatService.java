package csfyp.cs_fyp_android.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.LinkedList;
import java.util.List;

import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.model.Event;

import static android.view.View.GONE;
import static android.widget.ListPopupWindow.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;
import com.bumptech.glide.Glide;


/**
 * Created by ray on 8/2/2017.
 */

public class ChatService extends Service {
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

    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
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
            final com.github.clans.fab.FloatingActionButton btn = new com.github.clans.fab.FloatingActionButton(this);
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

                    mProgressBar.setVisibility(ProgressBar.VISIBLE);

                    mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,MessageViewHolder>(
                            FriendlyMessage.class,
                            R.layout.item_message,
                            MessageViewHolder.class,
                            mFirebaseDatabaseReference.child("messages/group_"+eventId)) { //TODO: change the child
                        @Override
                        protected FriendlyMessage parseSnapshot(DataSnapshot snapshot) {
                            FriendlyMessage friendlyMessage = super.parseSnapshot(snapshot);
                            if (friendlyMessage != null) {
                                friendlyMessage.setId(snapshot.getKey());
                            }
                            return friendlyMessage;
                        }
                        @Override
                        protected void populateViewHolder(MessageViewHolder viewHolder,
                                                          FriendlyMessage friendlyMessage, int position) {
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            viewHolder.messageTextView.setText(friendlyMessage.getContent());
                            viewHolder.messengerTextView.setText(friendlyMessage.getDisplayName());
                            if (friendlyMessage.getPhotoUrl() == null) {
                                viewHolder.messengerImageView
                                        .setImageDrawable(getResources().
                                                getDrawable(
                                                        R.drawable.ic_account_circle_black_36dp));
                            } else {
                                Glide.with(ChatService.this)
                                        .load(friendlyMessage.getPhotoUrl())
                                        .into(viewHolder.messengerImageView);
                            }
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
                    //TODO: change mChatBox adaptor
                    mStatus = 2;
                }
            });
            btn.setImageResource(R.drawable.bg_event);
            mFloatingActionButtonList.add(btn);
        }

        //handling open the menu
        mFloatingActionMenu = (FloatingActionMenu) mView.findViewById(R.id.floatingMsgMenu);
        //Cannot use because not covering whole screen
        //mFloatingActionMenu.setClosedOnTouchOutside(true);


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

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isAClick = true;
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
                            clickEvent();
                            return false;
                        }
                        else {
                            mParams.x = mParams.x + (int)(endX-preX);
                            mParams.y = mParams.y + (int)(endY-preY);
                            mWindowManager.updateViewLayout(mView,mParams);
                            return false;
                        }

                    case MotionEvent.ACTION_MOVE:
                        if(!isClick(startX,endX,startY,endY)) {
                            isAClick = false;
                        }
                        mParams.x = mParams.x + (int)(endX-preX);
                        mParams.y = mParams.y + (int)(endY-preY);
                        mWindowManager.updateViewLayout(mView, mParams);
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

    public void login(){
        Log.d("here",mMsgToken);
        mAuth.signInWithCustomToken(mMsgToken)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Log.d("login firebase: ", "login failed");
                        }
                    }
                });
    }

    public void setmEventList(List<Event> mEventList) {
        this.mEventList = mEventList;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startMsg();
            }
        });
    }

    public List<Event> getmEventList() {
        return mEventList;
    }

    private void clickEvent(){
        if(mStatus == 0) {
            for (com.github.clans.fab.FloatingActionButton _fab : mFloatingActionButtonList) {
                mFloatingActionMenu.addMenuButton(_fab);
            }
            /*
            int tempx = mParams.x;
            int tempy = mParams.y;
            mParams.x = 0;
            mParams.y = 0;
            mWindowManager.updateViewLayout(mView,mParams);
            mParams.x = tempx;
            mParams.y = tempy;
            */
            mStatus = 1;
        }
        else if(mStatus == 1) {
            for (com.github.clans.fab.FloatingActionButton _fab : mFloatingActionButtonList) {
                mFloatingActionMenu.removeMenuButton(_fab);
                //mWindowManager.updateViewLayout(mView,mParams);
            }
            mStatus = 0;
        }else if(mStatus == 2){
            for (com.github.clans.fab.FloatingActionButton _fab : mFloatingActionButtonList) {
                mFloatingActionMenu.removeMenuButton(_fab);
            }
            mChatBox.findViewById(R.id.chat_frame).setVisibility(GONE);
            mStatus = 0;
        }
        mFloatingActionMenu.toggle(true);
    }
}
