package csfyp.cs_fyp_android.lib.eventBus;

import java.util.List;

import csfyp.cs_fyp_android.chat.ChatService;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.User;

/**
 * Created by ray on 13/3/2017.
 */

public class ChatServiceSetting {
    public static final int INIT = 0;
    public static final int SET_PARAM= 1;
    public static final int UPDATE_TOKEN= 2;

    private int mode;
    private int delay;
    private List<Event> mEventList;
    private User mSelf;

    public ChatServiceSetting(int mode,List<Event> mEventList,User mSelf){
        this.mode = mode;
        this.mEventList = mEventList;
        this.mSelf = mSelf;
        this.delay = 0;
    }

    public ChatServiceSetting(int mode){
        this.mode = mode;
    }

    public List<Event> getmEventList() {
        return mEventList;
    }

    public int getMode() {
        return mode;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public void setmSelf(User mSelf) {
        this.mSelf = mSelf;
    }

    public User getmSelf() {
        return mSelf;
    }
}
