package csfyp.cs_fyp_android.model;

import android.location.Location;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import csfyp.cs_fyp_android.lib.TimeConverter;
import csfyp.cs_fyp_android.model.request.Rate;

public class Event {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String place;
    private User holder;
    private int holderId;
    private int maxPpl;
    private int minPpl;
    private int currentPpl;   // not in DB
    private String startTime_formated;
    private String deadlineTime_formated;
    private String description;
    private List<User> participantList;
    private List<Rate> rates;
    private List<Participation> attendance;

    public void setId(int id) {
        this.id = id;
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

    public Date getStartTimeInDate(){
        SimpleDateFormat foreignFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm");
        foreignFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = null;
        try {
            time = foreignFormat.parse(startTime_formated);
            return time;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return new Date();

    }

    public String getStartTime_formated() {
        //return startTime_formated;
        return TimeConverter.removeYear(TimeConverter.UTCToLocal(startTime_formated));
    }

    public String getDeadlineTime_formated() {
        //return deadlineTime_formated;
        return TimeConverter.removeYear(TimeConverter.UTCToLocal(deadlineTime_formated));
    }

    public String getDescription() {
        return description;
    }

    public List<User> getParticipantList() {
        return participantList;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public List<Participation> getAttendace() {
        return attendance;
    }

    public Location retrieveLocation(){
        Location locationA = new Location(this.name);
        locationA.setLatitude(this.getLatitude());
        locationA.setLongitude(this.getLongitude());
        return locationA;
    }

}
