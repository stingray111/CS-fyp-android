package csfyp.cs_fyp_android.model.request;

public class EventJoinQuitRequest {
    private int eventId;
    private int userId;

    public EventJoinQuitRequest(int eventId, int userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
