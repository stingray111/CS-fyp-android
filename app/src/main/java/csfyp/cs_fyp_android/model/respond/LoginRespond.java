package csfyp.cs_fyp_android.model.respond;

public class LoginRespond {
    private boolean isSuccessful;
    private String errorMsg;
    private int userId;
    private String token;

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}