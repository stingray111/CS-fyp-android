package csfyp.cs_fyp_android.model.request;

public class EventFilter {
    private double latitude;
    private double longitude;
    private int mode;
    // 1 for all in range
    // 2 for history
    // 3 for joined on going

    public EventFilter(double latitude, double longitude, int mode){
        this.latitude = latitude;
        this.longitude = longitude;
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
}
