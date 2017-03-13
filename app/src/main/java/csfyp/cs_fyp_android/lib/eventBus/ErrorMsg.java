package csfyp.cs_fyp_android.lib.eventBus;

import android.widget.Toast;

/**
 * Created by ray on 13/3/2017.
 */

public class ErrorMsg {
    private String errorMsg;
    public int duration;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;

    public ErrorMsg(String err,int duration){
        this.errorMsg = err;
        this.duration = duration;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getDuration() {
        return duration;
    }
}
