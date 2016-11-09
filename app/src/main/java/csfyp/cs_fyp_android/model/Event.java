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

    public String getName() {
        return name;
    }

    public void setName(String eventName) {
        this.name = eventName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getHolderId() {
        return holderId;
    }

    public void setHolderId(int holderId) {
        this.holderId = holderId;
    }

    public int getMaxPpl() {
        return maxPpl;
    }

    public void setMaxPpl(int maxPpl) {
        this.maxPpl = maxPpl;
    }

    public int getMinPpl() {
        return minPpl;
    }

    public void setMinPpl(int minPpl) {
        this.minPpl = minPpl;
    }

    public int getCurrentPpl() {
        return currentPpl;
    }

    public void setCurrentPpl(int minPpl) {
        this.currentPpl = minPpl;
    }

    public String getEventStart() {
        return eventStart_formated;
    }

    public void setEventStart(String eventStart) {
        this.eventStart_formated = eventStart;
    }

    public String getEventDeadline() {
        return eventDeadline_formated;
    }

    public void setEventDeadline(String eventDeadline) {
        this.eventDeadline_formated = eventDeadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
