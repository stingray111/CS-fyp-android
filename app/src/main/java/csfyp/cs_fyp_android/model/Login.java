package csfyp.cs_fyp_android.model;

public class Login {
    private String usernameOrEMail;
    private String password;
    private String UUID;
    private String platform;

    public Login(String usernameOrEMail, String password, String UUID) {
        this.usernameOrEMail = usernameOrEMail;
        this.password = password;
        this.UUID = UUID;
        this.platform = "Android";
    }

    public String getUsernameOrEMail() {
        return usernameOrEMail;
    }

    public String getPassword() {
        return password;
    }

    public String getUUID() {
        return UUID;
    }

    public String getPlatform() {
        return platform;
    }
}
