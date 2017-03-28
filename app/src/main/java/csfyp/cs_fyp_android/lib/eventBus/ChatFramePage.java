package csfyp.cs_fyp_android.lib.eventBus;

import csfyp.cs_fyp_android.model.User;

/**
 * Created by ray on 29/3/2017.
 */

public class ChatFramePage {
    private User mSelf;
    private int mEventId;
    private String mEventName;

    public ChatFramePage(User user, int mEventId, String mEventName){
        this.mSelf = user;
        this.mEventId = mEventId;
        this.mEventName = mEventName;
    }

    public User getmSelf() {
        return mSelf;
    }

    public int getmEventId() {
        return mEventId;
    }

    public String getmEventName() {
        return mEventName;
    }
}
