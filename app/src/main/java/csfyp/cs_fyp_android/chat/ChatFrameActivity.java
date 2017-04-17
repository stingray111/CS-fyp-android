package csfyp.cs_fyp_android.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.Utils;
import csfyp.cs_fyp_android.lib.eventBus.ChatFramePage;
import csfyp.cs_fyp_android.model.User;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

    //private User mSelf;
    public static User mSelf;
    private int mEventId;
    private String mEventName;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        chatFrameActivity = ChatFrameActivity.this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(TAG,"on Create");

        Log.d(TAG,"frame create intent"+getIntent().toString());

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
                final public View.OnClickListener _this = this;
                @Override
                public void onClick(final View view) {
                    view.setOnClickListener(null);
                    FriendlyMessage friendlyMessage = new
                            FriendlyMessage(mSelf.getUserName(),
                            mSelf.getDisplayName(),
                            mMessageEditText.getText().toString(),
                            mSelf.getProPic());

                    mFirebaseDatabaseReference.child("messages/group_" + mEventId)
                            .push().setValue(friendlyMessage, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d(TAG, "message sent fail: " + databaseError.getMessage());
                            }else{
                                databaseReference.child("reachServer").keepSynced(true);
                                databaseReference.child("reachServer").setValue(true);
                                mFirebaseAdapter.notifyItemChanged(mFirebaseAdapter.getItemCount()-1);
                            }
                        }
                    });
                    mMessageEditText.setText("");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setOnClickListener(_this);
                        }
                    }, 1500);
                }
            });

            ((TextView) findViewById(R.id.chatFrameTitle)).setText(mEventName);
            mProgressBar.setVisibility(VISIBLE);

            DatabaseReference list = mFirebaseDatabaseReference.child("messages/group_" + mEventId) ;

            mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                    FriendlyMessage.class,
                    R.layout.item_message,
                    MessageViewHolder.class,
                    mFirebaseDatabaseReference.child("messages/group_" + mEventId)) {

                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage model, int position) {
                    int localType = getLocalType(model);
                    switch (localType) {
                        case 3:
                        case 13:
                        case 4:
                        case 14:
                        case 0:
                            //others
                            mProgressBar.setVisibility(ProgressBar.GONE);
                            viewHolder.messageTextView.setText(model.getContent());
                            viewHolder.messengerTextView.setText(model.getDisplayName());
                            viewHolder.timeStamp.setText(model.getTime());
                            // load propic
                            int size = Utils.dpToPx(getBaseContext(), 36);
                            Picasso.with(getBaseContext())
                                    .load(model.getPhotoUrl())
                                    .resize(size, size)
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_propic_big)
                                    .into(viewHolder.messengerImageView);
                            break;
                        case 10:
                            //own
                            mProgressBar.setVisibility(ProgressBar.GONE);
                            viewHolder.messageTextView.setText(model.getContent());
                            viewHolder.timeStamp.setText(model.getTime());
                            if(model.isReachServer()){
                                viewHolder.uploadingProgress.setVisibility(GONE);
                            }else {
                                viewHolder.uploadingProgress.setVisibility(VISIBLE);
                            }
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
                public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    switch (viewType) {
                        case 10:
                            View selfView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_message_own, parent, false);
                            return new MessageViewHolder(selfView);
                        case 3:
                        case 13:
                        case 4:
                        case 14:
                        case 0:
                            View otherView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_message, parent, false);
                            return new MessageViewHolder(otherView);

                    }
                    Log.d(TAG, "unhandled");
                    return null;
                }

                @Override
                public void onBindViewHolder(MessageViewHolder viewHolder, int position) {
                    super.onBindViewHolder(viewHolder, position);
                    long type = this.getItem(position).getType();
                    if(type == 3 || type == 13) {
                        viewHolder.date.setText("Group Created");
                        viewHolder.date.setVisibility(VISIBLE);
                        viewHolder.date.setPadding(0, 0, 0, 10);
                        viewHolder.contentRoot.setVisibility(GONE);
                    }else if(type == 4 || type == 14){
                        String content = this.getItem(position).getContent();
                        viewHolder.date.setText(content);
                        viewHolder.date.setVisibility(VISIBLE);
                        viewHolder.date.setPadding(0, 0, 0, 10);
                        viewHolder.contentRoot.setVisibility(GONE);
                    }else {
                        viewHolder.contentRoot.setVisibility(VISIBLE);
                        final Date thisMessageDate = this.getItem(position).getDate();
                        final String thisMessageDay = (String) DateFormat.format("dd-MMM-yyyy", thisMessageDate);
                        if (position != 0) {
                            final Date previousMessageDate = this.getItem(position - 1).getDate();
                            //set date
                            final String previousMessageDay = (String) DateFormat.format("dd-MMM-yyyy", previousMessageDate);
                            if (!thisMessageDay.equals(previousMessageDay)) {
                                viewHolder.date.setVisibility(VISIBLE);
                                viewHolder.date.setText(thisMessageDay);
                            } else {
                                viewHolder.date.setVisibility(GONE);
                            }
                        } else {
                            viewHolder.date.setVisibility(VISIBLE);
                            viewHolder.date.setText(thisMessageDay);
                        }
                    }
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

            list.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mProgressBar.setVisibility(GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

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
        Log.d(TAG,"Frame new intent" + intent.toString());
    }

}
