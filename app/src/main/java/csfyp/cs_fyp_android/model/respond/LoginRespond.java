package csfyp.cs_fyp_android.model.respond;

import csfyp.cs_fyp_android.model.User;

public class LoginRespond {
    private boolean isSuccessful;
    private String errorMsg;
    private int userId;
    private String username;
    private String token;
    private String msgToken;
    private User self;

    public String getMsgToken(){
        return msgToken;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public User getSelf() {
        return self;
    }
}
