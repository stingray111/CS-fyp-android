package csfyp.cs_fyp_android.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Event {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String place;
    private int holderId;
    private int maxPpl;
    private int minPpl;
    private int currentPpl;   // not in DB
    private String eventStart_formated;
    private String eventDeadline_formated;
    private String description;

    public Event(String eventName, double latitude, double longitude, String place, int holderId, int currentPpl, int maxPpl, int minPpl, String description) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm");
        this.name = eventName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.holderId = holderId;
        this.place = place;
        this.maxPpl = maxPpl;
        this.minPpl = minPpl;
        this.currentPpl = currentPpl;
        this.eventStart_formated = df.format(new Timestamp(System.currentTimeMillis()));
        this.eventDeadline_formated = df.format(new Timestamp(System.currentTimeMillis()));
        this.description = description;
    }

    public Event(String eventName, double latitude, double longitude, String place, int holderId, int currentPpl, int maxPpl, int minPpl,
                 String eventStart, String eventDeadline, String description) {
        this.name = eventName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
        this.holderId = holderId;
        this.maxPpl = maxPpl;
        this.minPpl = minPpl;
        this.currentPpl = currentPpl;
        this.eventStart_formated = eventStart;
        this.eventDeadline_formated = eventDeadline;
        this.description = description;
    }

    public int getId() {
        return id;
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

    public int getCurrentPpl() {
        return currentPpl;
    }

    public String getEventStart_formated() {
        return eventStart_formated;
    }

    public String getEventDeadline_formated() {
        return eventDeadline_formated;
    }

    public String getDescription() {
        return description;
    }
}
