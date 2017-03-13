package csfyp.cs_fyp_android.lib.eventBus;

import java.util.List;

import csfyp.cs_fyp_android.chat.ChatService;
import csfyp.cs_fyp_android.model.Event;

/**
 * Created by ray on 13/3/2017.
 */

public class ChatServiceSetting {
    public static final int INIT = 0;
    public static final int SET_PARAM= 1;
    private int mode;
    private List<Event> mEventList;
    private String mMsgToken;

    public ChatServiceSetting(int mode,List<Event> mEventList,String mMsgToken){
        this.mode = mode;
        this.mEventList = mEventList;
        this.mMsgToken = mMsgToken;
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

    public String getmMsgToken() {
        return mMsgToken;
    }
}
