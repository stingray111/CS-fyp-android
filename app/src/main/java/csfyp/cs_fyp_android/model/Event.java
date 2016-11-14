package csfyp.cs_fyp_android.model;

import java.util.List;

public class Event {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String place;
    private User holder;
    private int maxPpl;
    private int minPpl;
    private int currentPpl;   // not in DB
    private String startTime_formated;
    private String deadlineTime_formated;
    private String description;
    private List<User> participantList;


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

    public User getHolder() {
        return holder;
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

    public String getStartTime_formated() {
        return startTime_formated;
    }

    public String getDeadlineTime_formated() {
        return deadlineTime_formated;
    }

    public String getDescription() {
        return description;
    }

    public List<User> getParticipantList() {
        return participantList;
    }
}
