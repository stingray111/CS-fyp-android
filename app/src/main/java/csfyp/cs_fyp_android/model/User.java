package csfyp.cs_fyp_android.model;

public class User {
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
    private String fullName;
    private int level;

    public User(String userName, String password, String firstName, String lastName, String nickName, boolean isMale, int attendEventNum, int missingEventNum, int holdingEventNum, String email, String phone, String description,int level){
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.isMale = isMale;
        this.attendEventNum = attendEventNum;
        this.missingEventNum = missingEventNum;
        this.holdingEventNum = holdingEventNum;
        this.email =  email;
        this.phone = phone;
        this.description = description;
        this.fullName = firstName+" "+lastName+" ("+nickName+") ";
        this.level = level;
    }

    public String getUserName() {
        return userName;
    }
    public String getFirstName(){ return this.firstName;}
    public String getLastName(){ return this.lastName;}
    public String getNickName(){ return this.nickName;}
    public boolean getIsMale(){ return this.isMale;}
    public int getAttendEventNum(){ return this.attendEventNum;}
    public int getMissingEventNum(){ return this.missingEventNum;}
    public int getHoldingEventNum(){ return this.holdingEventNum;}
    public String getEmail(){ return this.email;}
    public String getPhone(){ return this.phone;}
    public String getDescription(){ return this.description;}
    public String getFullName(){ return this.fullName;}
    public int getLevel(){return this.level;}

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setFirstName(String first){ this.firstName =first; }
    public void setLastName(String last){ this.lastName=last; }
    public void setNickName(String nick){ this.nickName=nick; }
    public void setIsMale(boolean gender){ this.isMale=gender; }
    public void setMissingEventNum(int missing){ this.missingEventNum=missing; }
    public void setAttendEventNum(int attend){ this.attendEventNum=attend; }
    public void setHoldingEventNum(int hold){ this.holdingEventNum=hold; }
    public void setEmail(String emailaddr){ this.email=emailaddr; }
    public void setPhone(String phonenum){ this.phone=phonenum; }
    public void setDescription(String descript){ this.description=descript; }
    public void setFullName(String fullName){this.fullName=fullName;}
    public void setLevel(int level){this.level= level;}

}
