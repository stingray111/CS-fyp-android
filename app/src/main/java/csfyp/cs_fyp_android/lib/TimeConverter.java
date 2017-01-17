package csfyp.cs_fyp_android.lib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ray on 17/1/2017.
 */

public class TimeConverter {
    public static String localToUTC(String localTime){
        String UTCTime=null;
        SimpleDateFormat localFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        localFormat.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat foreignFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        foreignFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = null;
        try {
            time = localFormat.parse(localTime);
        }catch(Exception e){
        }
        UTCTime = foreignFormat.format(time);
        return UTCTime;
    }

    public static String UTCToLocal(String localTime){
        String UTCTime=null;
        SimpleDateFormat localFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        localFormat.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat foreignFormat= new SimpleDateFormat("yyyy/MM/dd HH:mm");
        foreignFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = null;
        try {
            time = foreignFormat.parse(localTime);
        }catch(Exception e){
        }
        UTCTime = localFormat.format(time);
        return UTCTime;
    }

    public static String removeYear(String input){
        // assume input is YYYY/MM/DD HH:mm
        String temp = input.substring(5);
        String month = temp.substring(0,2);
        String day = temp.substring(3,5);
        String time = temp.substring(5);
        return day+"/"+month+time;
    }

}
