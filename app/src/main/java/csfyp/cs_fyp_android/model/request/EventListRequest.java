package csfyp.cs_fyp_android.model.request;

public class EventListRequest {
    private int offset;
    private double latitude;
    private double longitude;
    private double userId;
    private long startAt;
    private int mode;
    // 1 for all in range
    // 2 for history
    // 3 for joined on going

    public EventListRequest(double latitude, double longitude, int mode, int offset, long startAt){
        this.latitude = latitude;
        this.longitude = longitude;
        this.mode = mode;
        this.offset = offset;
        this.startAt = startAt;
    }

    public EventListRequest(int userId, int mode) {
        this.userId = userId;
        this.mode = mode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getMode() {
        return mode;
    }

    public int getOffset() {
        return offset;
    }
}
