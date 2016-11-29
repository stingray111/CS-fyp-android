package csfyp.cs_fyp_android.model;

public class User {
    private int id;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String nickName;
    private boolean isMale;
    private int attendEventNum;
    private int missingEventNum;
    private int holdingEventNum;
    private String email;
    private String phone;
    private String description;
    private int level;

    public User(String userName, String password, String firstName, String lastName, String nickName, boolean isMale, int attendEventNum, int missingEventNum, int holdingEventNum, String email, String phone, String description, int level) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.isMale = isMale;
        this.attendEventNum = attendEventNum;
        this.missingEventNum = missingEventNum;
        this.holdingEventNum = holdingEventNum;
        this.email = email;
        this.phone = phone;
        this.description = description;
        this.level = level;
    }

    public String getFullName(){
        if(nickName != null && firstName!=null){
            return firstName+" "+lastName+" ("+nickName+")";
        }
        else if(nickName!=null){
            return lastName+" ("+nickName+")";
        }
        else if(firstName!=null){
            return firstName+" "+lastName;
        }
        return lastName;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getNickName() {
        return this.nickName;
    }

    public boolean getIsMale() {
        return this.isMale;
    }

    public int getAttendEventNum() {
        return this.attendEventNum;
    }

    public int getMissingEventNum() {
        return this.missingEventNum;
    }

    public int getHoldingEventNum() {
        return this.holdingEventNum;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getDescription() {
        return this.description;
    }

    public int getLevel() {
        return this.level;
    }

}
