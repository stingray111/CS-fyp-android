package csfyp.cs_fyp_android.model.respond;

/**
 * Created by ray on 22/3/2017.
 */

public class MsgTokenUpdateRespond {
    private String errorMsg;
    private String msgToken;
    private boolean isSuccessful;

    public String getMsgToken() {
        return msgToken;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
