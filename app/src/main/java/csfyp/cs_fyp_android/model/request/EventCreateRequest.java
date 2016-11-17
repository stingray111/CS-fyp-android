package csfyp.cs_fyp_android.model.request;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class EventCreateRequest {
    private String name;
    private double latitude;
    private double longitude;
    private String place;
    private int holderId;
    private int maxPpl;
    private int minPpl;
    private String eventStart;
    private String eventDeadline;
    private String description;

    public EventCreateRequest(String name, double latitude, double longitude, String place, int holderId, int maxPpl, int minPpl, String description) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm");
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
        this.holderId = holderId;
        this.maxPpl = maxPpl;
        this.minPpl = minPpl;
        this.eventStart = df.format(new Timestamp(System.currentTimeMillis()+1000000000));
        this.eventDeadline = df.format(new Timestamp(System.currentTimeMillis()+100000000));
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPlace() {
        return place;
    }

    public int getHolderId() {
        return holderId;
    }

    public int getMaxPpl() {
        return maxPpl;
    }

    public int getMinPpl() {
        return minPpl;
    }

    public String getEventStart() {
        return eventStart;
    }

    public String getEventDeadline() {
        return eventDeadline;
    }

    public String getDescription() {
        return description;
    }
}
