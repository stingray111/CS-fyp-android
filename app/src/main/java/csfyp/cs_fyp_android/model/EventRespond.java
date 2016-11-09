package csfyp.cs_fyp_android.model;

import java.util.List;

public class EventRespond {
    String errorMsg;
    List<Event> data;

    public String getErrorMsg() {
        return errorMsg;
    }

    public List<Event> getEvents() {
        return data;
    }
}
