package csfyp.cs_fyp_android.model;

public class Login {
    private String usernameOrEmail;
    private String password;
    private String UUID;
    private String platform;

    public Login(String usernameOrEmail, String password, String UUID) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
        this.UUID = UUID;
        this.platform = "Android";
    }

    public String getUsernameOrEMail() {
        return usernameOrEmail;
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
