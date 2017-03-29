package csfyp.cs_fyp_android.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.Utils;
import csfyp.cs_fyp_android.lib.eventBus.ChatFramePage;
import csfyp.cs_fyp_android.model.User;

/**
 * Created by ray on 27/3/2017.
 */

public class ChatFrameActivity extends Activity {
    public volatile static boolean active = false;
    public static Activity chatFrameActivity;

    private static String TAG = "ChatFrameActivity";
    private Button mSendButton;
    private EditText mMessageEditText;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mMessageRecyclerView;

    private User mSelf;
    private int mEventId;
    private String mEventName;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, RecyclerView.ViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        chatFrameActivity = ChatFrameActivity.this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(TAG,"on Create");

        setContentView(R.layout.chat_frame);

        //init chat box
        mMessageEditText = (EditText)findViewById(R.id.messageEditText);
        mSendButton = (Button)findViewById(R.id.sendButton);
        mProgressBar = (ProgressBar) findViewById(R.id.chatFrameProgressBar);

        findViewById(R.id.chat_inner).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });

        findViewById(R.id.chatFrameBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFrameActivity.this.onBackPressed();
            }
        });

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);

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

    }

    @Override
    protected void onStart() {
        Log.d(TAG,"on start");
        super.onStart();
        EventBus.getDefault().register(this);
        active = true;
        EventBus.getDefault().post(new ChatFramePage(ChatFramePage.REQUEST));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPage(ChatFramePage chatFramePage){
        if(chatFramePage.mode == ChatFramePage.PROVIDE_DATA) {
            Log.d(TAG,"set the values");
            this.mEventName = chatFramePage.getmEventName();
            this.mEventId = chatFramePage.getmEventId();
            this.mSelf = chatFramePage.getmSelf();

            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FriendlyMessage friendlyMessage = new
                            FriendlyMessage(mSelf.getUserName(),
                            mSelf.getDisplayName(),
                            mMessageEditText.getText().toString(),
                            mSelf.getProPic());
                    mFirebaseDatabaseReference.child("messages/group_" + mEventId)
                            .push().setValue(friendlyMessage);
                    mMessageEditText.setText("");
                }
            });

            ((TextView) findViewById(R.id.chatFrameTitle)).setText(mEventName);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);

            mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, RecyclerView.ViewHolder>(
                    FriendlyMessage.class,
                    R.layout.item_message,
                    RecyclerView.ViewHolder.class,
                    mFirebaseDatabaseReference.child("messages/group_" + mEventId)) {
                @Override
                protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, FriendlyMessage model, int position) {
                    int localType = getLocalType(model);
                    switch (localType) {
                        case 0:
                            //others
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            ((MessageViewHolder) viewHolder).messageTextView.setText(model.getContent());
                            ((MessageViewHolder) viewHolder).messengerTextView.setText(model.getDisplayName());
                            ((MessageViewHolder) viewHolder).timeStamp.setText(model.getTime());
                            // load propic
                            int size = Utils.dpToPx(getBaseContext(), 36);
                            Picasso.with(getBaseContext())
                                    .load(model.getPhotoUrl())
                                    .resize(size, size)
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_propic_big)
                                    .into(((MessageViewHolder) viewHolder).messengerImageView);
                            //TODO
                            break;
                        case 10:
                            //own
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            ((MessageViewHolder) viewHolder).messageTextView.setText(model.getContent());
                            ((MessageViewHolder) viewHolder).timeStamp.setText(model.getTime());
                            break;
                    }
                }

                @Override
                protected FriendlyMessage parseSnapshot(DataSnapshot snapshot) {
                    FriendlyMessage friendlyMessage = super.parseSnapshot(snapshot);
                    if (friendlyMessage != null) {
                        friendlyMessage.setId(snapshot.getKey());
                    }
                    return friendlyMessage;
                }

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    switch (viewType) {
                        case 10:
                            View selfView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_message_own, parent, false);
                            return new MessageViewHolder(selfView);
                        case 0:
                            View otherView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_message, parent, false);
                            return new MessageViewHolder(otherView);
                    }
                    Log.d(TAG, "unhandled");
                    return null;
                }


                @Override
                public int getItemViewType(int position) {
                    FriendlyMessage friendlyMessage = getItem(position);
                    return getLocalType(friendlyMessage);
                }

                public int getLocalType(FriendlyMessage friendlyMessage) {
                    int localType = 0;
                    if (friendlyMessage.getUid().equals(MainActivity.mUsername)) { //TODO
                        localType = friendlyMessage.getType() + 10;
                    } else {
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
                    int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
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

            if (mMessageRecyclerView.getAdapter() == null) {
                mMessageRecyclerView.setAdapter(mFirebaseAdapter);
            } else {
                mMessageRecyclerView.swapAdapter(mFirebaseAdapter, false);
            }
        }

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d(TAG,"on resume");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d(TAG,"on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.d(TAG,"on stop");
        active = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG,"on destroy");
        active = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d(TAG,"on new intent");
    }

}
