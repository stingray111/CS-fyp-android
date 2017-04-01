package csfyp.cs_fyp_android.lib.eventBus;

/**
 * Created by ray on 29/3/2017.
 */

public class SnackBarMessageContent {
    public String message;
    public String action;

    public SnackBarMessageContent(String message){
        this.message = message;
    }

    public SnackBarMessageContent(String message, String action) {
        this.message = message;
        this.action = action;
    }
}
