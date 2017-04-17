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
    public static final int ADD_EVENT= 3;
    public static final int REMOVE_EVENT= 4;
    public static final int CallChatFrame= 5;
    public static final int SWAP_EVENT_LIST = 6;

    private int mode;
    private int delay;
    private List<Event> mEventList;
    private User mSelf;

    //for add event
    private Event eventObj;

    //for remove event
    private int rmEventId;

    public ChatServiceSetting(int mode,List<Event> mEventList,User mSelf){
        this.mode = mode;
        this.mEventList = mEventList;
        this.mSelf = mSelf;
        this.delay = 0;
    }

    public ChatServiceSetting(List<Event> mEventList,int mode){
        this.mode = mode;
        this.mEventList = mEventList;
    }

    public ChatServiceSetting(Event event, int mode){
        this.eventObj = event;
        this.mode = mode;
    }

    public ChatServiceSetting(Event eventToBeAdded){
        this.eventObj = eventToBeAdded;
        mode = ADD_EVENT;
    }

    public ChatServiceSetting(int eventIdToBeRemoved,int mode){
        this.rmEventId = eventIdToBeRemoved;
        this.mode = REMOVE_EVENT;
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

    public Event getEventObj() {
        return eventObj;
    }

    public int getRmEventId() {
        return rmEventId;
    }
}
