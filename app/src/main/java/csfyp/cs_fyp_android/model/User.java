package csfyp.cs_fyp_android.model;

/**
 * Created by ray on 27/10/2016.
 */

public class User {
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

    public User(String first, String last, String nick, boolean gender, int attend, int miss, int hold, String mailaddr, String phonenum, String descriptions,int level){
        this.firstName=first;
        this.lastName=last;
        this.nickName=nick;
        this.isMale = gender;
        this.attendEventNum =attend;
        this.missingEventNum= miss;
        this.holdingEventNum = hold;
        this.email =  mailaddr;
        this.phone = phonenum;
        this.description = descriptions;
        this.fullName = firstName+" "+lastName+" ("+nickName+") ";
        this.level = level;
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
