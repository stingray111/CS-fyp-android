package csfyp.cs_fyp_android.model;

public class Participation {
    private int userId;
    private int eventId;
    private boolean attendance;

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public boolean isAttended() {
        return attendance;
    }
}
