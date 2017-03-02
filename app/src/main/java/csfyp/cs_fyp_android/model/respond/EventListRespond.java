package csfyp.cs_fyp_android.model.respond;

import java.util.List;

import csfyp.cs_fyp_android.model.Event;

public class EventListRespond {
    String errorMsg;
    List<Event> data;
    long startId;

    public String getErrorMsg() {
        return errorMsg;
    }

    public List<Event> getEvents() {
        return data;
    }

    public long getStartId() {
        return startId;
    }
}
