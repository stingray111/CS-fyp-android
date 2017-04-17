package csfyp.cs_fyp_android.chat;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.Utils;
import csfyp.cs_fyp_android.lib.eventBus.ChatServiceSetting;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.User;

/**
 * Created by ray on 12/4/2017.
 */

public class ChatFrgAdt extends RecyclerView.Adapter<ChatGroupsViewHolder>{
    private List<Event> mEventList;
    private Context context;
    private DatabaseReference firebaseDbRef = FirebaseDatabase.getInstance().getReference();

    public ChatFrgAdt(List<Event> mEventList, Context context ){
        this.mEventList = mEventList;
        this.context = context;
    }

    @Override
    public ChatGroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View selfView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_frg_rv_vh, parent, false);
        return new ChatGroupsViewHolder(selfView);
    }

    @Override
    public void onBindViewHolder(final ChatGroupsViewHolder holder, int position) {
        final Event item = mEventList.get(position);
        holder.groupName.setText(item.getName());
        holder.lastMsgContent.setText("loading");
        holder.lastMsgTime.setText("loading");
        holder.lastMsgSender.setText("loading");

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ChatServiceSetting(item,ChatServiceSetting.CallChatFrame));
            }
        });

        int size = Utils.dpToPx(context, 36);
        Picasso.with(context)
                .load(item.getHolder().getProPic())
                .resize(size, size)
                .centerCrop()
                .placeholder(R.drawable.ic_propic_big)
                .into(holder.messengerImageView);

        firebaseDbRef.child("messages/group_"+item.getId()).limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null) {
                    try {
                        Date now = new Date();
                        Date date = new Date((long) dataSnapshot.child("creationDate").getValue());

                        SimpleDateFormat sfd = new SimpleDateFormat("d MMM yyyy");
                        String strDate = sfd.format(date);
                        String strNow = sfd.format(now);
                        if(strDate.equals(strNow)){
                            sfd = new SimpleDateFormat("HH:mm");
                            String strTime = sfd.format(date);
                            holder.lastMsgTime.setText(strTime);
                        }else{
                            holder.lastMsgTime.setText(strDate);
                        }

                        long type = (long)dataSnapshot.child("type").getValue();
                        String sender = (String) dataSnapshot.child("displayName").getValue();
                        if(type == 3) {
                            holder.lastMsgSender.setText(sender+" created the group");
                            holder.lastMsgContent.setText("");
                        }else {
                            holder.lastMsgContent.setText((String) dataSnapshot.child("content").getValue());
                            holder.lastMsgSender.setText(sender+" :");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if(mEventList!=null){
            return mEventList.size();
        }else {
            return 0;
        }
    }

}
