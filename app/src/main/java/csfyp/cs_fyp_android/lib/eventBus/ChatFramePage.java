package csfyp.cs_fyp_android.lib.eventBus;

import csfyp.cs_fyp_android.model.User;

/**
 * Created by ray on 29/3/2017.
 */

public class ChatFramePage {
    public static int REQUEST = 0;
    public static int PROVIDE_DATA = 1;
    private User mSelf;
    private int mEventId;
    private String mEventName;
    public int mode;

    public ChatFramePage(User mSelf, int mEventId, String mEventName){
        this.mSelf = mSelf;
        this.mEventId = mEventId;
        this.mEventName = mEventName;
        this.mode = PROVIDE_DATA;
    }

    public ChatFramePage(int mode){
        this.mode = mode;
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
