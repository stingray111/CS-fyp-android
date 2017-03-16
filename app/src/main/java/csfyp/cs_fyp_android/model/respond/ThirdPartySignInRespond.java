package csfyp.cs_fyp_android.model.respond;

import csfyp.cs_fyp_android.model.User;

/**
 * Created by ray on 16/3/2017.
 */

public class ThirdPartySignInRespond {
    private boolean isSignIn;
    private boolean isSuccessful;
    private String errorMsg;
    private int userId;
    private String username;
    private String token;
    private String msgToken;
    private User self;
    private int acType;

    public int getAcType() {
        return acType;
    }

    public User getSelf() {
        return self;
    }

    public String getToken() {
        return token;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getUserId() {
        return userId;
    }

    public String getMsgToken() {
        return msgToken;
    }

    public String getUsername() {
        return username;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public boolean isSignIn() {
        return isSignIn;
    }
}
