package csfyp.cs_fyp_android.model.request;

public class ChangeAttendanceRequest {
    int userId;
    int eventId;
    boolean attendance;

    public ChangeAttendanceRequest(int userId, int eventId, boolean attendance) {
        this.userId = userId;
        this.eventId = eventId;
        this.attendance = attendance;
    }
}
