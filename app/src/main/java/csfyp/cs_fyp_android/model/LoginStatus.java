package csfyp.cs_fyp_android.model;

public class LoginStatus {
    private boolean isSuccessful;
    private String errorMsg;
    private String token;

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getErrorMsg() {
        return errorMsg;
    }


    public String getToken() {
        return token;
    }
}
