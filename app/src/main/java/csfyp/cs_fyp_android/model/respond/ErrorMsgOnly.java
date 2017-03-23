package csfyp.cs_fyp_android.model.respond;

public class ErrorMsgOnly {
    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isNull(){
        return errorMsg == null;
    }
}
