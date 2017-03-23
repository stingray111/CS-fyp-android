package csfyp.cs_fyp_android.model.request;

import com.mobsandgeeks.saripaar.annotation.Email;

/**
 * Created by ray on 22/3/2017.
 */

public class EmailOnly {
    private String email;
    public EmailOnly(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
