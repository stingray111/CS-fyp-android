package csfyp.cs_fyp_android.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import csfyp.cs_fyp_android.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ray on 15/2/2017.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView messageTextView;
    public TextView messengerTextView;
    public TextView timeStamp;
    public TextView date;
    public CircleImageView messengerImageView;
    public ProgressBar uploadingProgress;

    public MessageViewHolder(View v) {
        super(v);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        try { messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView); }catch (Exception e){ }
        try { uploadingProgress = (ProgressBar) itemView.findViewById(R.id.messageUploading); }catch ( Exception e){ }
        timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
        date = (TextView) itemView.findViewById(R.id.DateStamp);
    }

}
