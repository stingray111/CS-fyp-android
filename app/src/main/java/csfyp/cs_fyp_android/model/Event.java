package csfyp.cs_fyp_android.model;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Event {
    private String eventName;
    private LatLng position;
    private String holderName;
    private int maxPpl;
    private int currentPpl;
    private String eventStart;
    private String eventEnd;
    private String eventDeadline;
    private String description;

    public Event(String eventName, LatLng position, String holderName, int currentPpl, int maxPpl, String description) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm");
        int time = (int) (System.currentTimeMillis());
        this.eventName = eventName;
        this.position = position;
        this.holderName = holderName;
        this.maxPpl = maxPpl;
        this.currentPpl = currentPpl;
        this.eventStart = df.format(new Timestamp(System.currentTimeMillis()));
        this.eventEnd = df.format(new Timestamp(System.currentTimeMillis()));
        this.eventDeadline = df.format(new Timestamp(System.currentTimeMillis()));
        this.description = description;
    }

    public Event(String eventName, LatLng position, String holderName, int currentPpl, int maxPpl,
                 String eventStart, String eventEnd, String eventDeadline, String description) {
        this.eventName = eventName;
        this.position = position;
        this.holderName = holderName;
        this.maxPpl = maxPpl;
        this.currentPpl = currentPpl;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.eventDeadline = eventDeadline;
        this.description = description;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public int getMaxPpl() {
        return maxPpl;
    }

    public void setMaxPpl(int maxPpl) {
        this.maxPpl = maxPpl;
    }

    public int getCurrentPpl() {
        return currentPpl;
    }

    public void setCurrentPpl(int minPpl) {
        this.currentPpl = minPpl;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getEventDeadline() {
        return eventDeadline;
    }

    public void setEventDeadline(String eventDeadline) {
        this.eventDeadline = eventDeadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
