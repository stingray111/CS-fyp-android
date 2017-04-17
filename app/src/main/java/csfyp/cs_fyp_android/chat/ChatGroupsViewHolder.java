package csfyp.cs_fyp_android.chat;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import csfyp.cs_fyp_android.R;

/**
 * Created by ray on 12/4/2017.
 */

public class ChatGroupsViewHolder extends RecyclerView.ViewHolder {
    public View rootView;
    public ImageView messengerImageView;
    public TextView groupName;
    public TextView lastMsgTime;
    public TextView lastMsgSender;
    public TextView lastMsgContent;
    public ChatGroupsViewHolder(View v){
        super(v);
        messengerImageView = (ImageView)v.findViewById(R.id.groupHolderProPic);
        lastMsgTime = (TextView)v.findViewById(R.id.lastMsgTime);
        groupName = (TextView)v.findViewById(R.id.groupName);
        lastMsgSender = (TextView)v.findViewById(R.id.lastMsgSender);
        lastMsgContent = (TextView)v.findViewById(R.id.lastMsgContent);
        rootView = v.findViewById(R.id.groupHolderRoot);
    }
}
