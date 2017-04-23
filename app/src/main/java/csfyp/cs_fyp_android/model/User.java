package csfyp.cs_fyp_android.model;

public class User {
    private int id;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String nickName;
    private String proPic;
    private int gender; //0:unknown 1:male 2:female
    private boolean isRatedbyOther;
    private boolean isSelfRated;
    private boolean isAttended = false;
    private int attendEventNum;
    private int missingEventNum;
    private int holdingEventNum;
    private String email;
    private String phone;
    private String description;
    private int acType;

    private float selfExtraversion;
    private float selfAgreeableness;
    private float selfConscientiousness;
    private float selfNeuroticism;
    private float selfOpenness;
    private float adjustmentExtraversionWeightedSum;
    private float adjustmentAgreeablenessWeightedSum;
    private float adjustmentConscientiousnessWeightedSum;
    private float adjustmentNeuroticismWeightedSum;
    private float adjustmentOpennessWeightedSum;
    private float adjustmentWeight;
    private String msgToken;
    private String apiToken;
    private int level;

    public User(String userName, String password, String firstName, String lastName, String nickName, boolean isMale, int attendEventNum, int missingEventNum, int holdingEventNum, String email, String phone, String description, int level) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        if(isMale) this.gender = 1;
        else this.gender = 2;
        this.attendEventNum = attendEventNum;
        this.missingEventNum = missingEventNum;
        this.holdingEventNum = holdingEventNum;
        this.email = email;
        this.phone = phone;
        this.description = description;
        this.level = level;
        this.isSelfRated = false;
        this.acType = 0;
    }

    public User(String userName, String password, String firstName, String lastName, String nickName, String propic, boolean isMale, int attendEventNum, int missingEventNum, int holdingEventNum, String email, String phone, String description, int level) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.proPic = propic;
        if(isMale) this.gender = 1;
        else this.gender = 2;
        this.attendEventNum = attendEventNum;
        this.missingEventNum = missingEventNum;
        this.holdingEventNum = holdingEventNum;
        this.email = email;
        this.phone = phone;
        this.description = description;
        this.level = level;
        this.isSelfRated = false;
        this.acType = 0;
    }
    public User(String userName, int actype, String firstName, String lastName, String proPic, int gender, String email){
        this.userName = userName;
        this.password = "";
        this.acType = actype;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = "";
        this.attendEventNum = 0;
        this.missingEventNum = 0;
        this.holdingEventNum = 0;
        this.phone = "";
        this.description = "";
        this.email = email;
        this.level = 1;
        this.gender = gender;
        this.proPic = proPic;
        this.isSelfRated = false;
        this.level = 1;
    }

    public User(int id, String firstName, String lastName, String nickName, String proPic, String phone, String description) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.proPic = proPic;
        this.phone = phone;
        this.description = description;
    }

    public String getFullName(){
        if(nickName != "" && nickName != null && firstName!=null && firstName != ""){
            return firstName+" "+lastName+" ("+nickName+")";
        }
        else if(nickName!=null && nickName!=""){
            return lastName+" ("+nickName+")";
        }
        else if(firstName!=null && lastName!=""){
            return firstName+" "+lastName;
        }
        return lastName;
    }

    public String getDisplayName(){
        if(nickName != null && nickName != "") {
            return nickName;
        }
        else if(firstName != null && firstName != "") {
            return firstName + " " + lastName;
        }
        else {
            return lastName;
        }
    }

    public String getMsgToken() {
        return msgToken;
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

    public String getProPic() {
        return proPic;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isSelfRated() {
        return isSelfRated;
    }

    public void setAttended(boolean attended) {
        isAttended = attended;
    }

    public float getSelfExtraversion() {
        return selfExtraversion;
    }

    public float getSelfAgreeableness() {
        return selfAgreeableness;
    }

    public float getSelfConscientiousness() {
        return selfConscientiousness;
    }

    public float getSelfNeuroticism() {
        return selfNeuroticism;
    }

    public float getSelfOpenness() {
        return selfOpenness;
    }

    public float getAdjustmentExtraversionWeightedSum() {
        return adjustmentExtraversionWeightedSum;
    }

    public float getAdjustmentAgreeablenessWeightedSum() {
        return adjustmentAgreeablenessWeightedSum;
    }

    public float getAdjustmentConscientiousnessWeightedSum() {
        return adjustmentConscientiousnessWeightedSum;
    }

    public float getAdjustmentNeuroticismWeightedSum() {
        return adjustmentNeuroticismWeightedSum;
    }

    public float getAdjustmentOpennessWeightedSum() {
        return adjustmentOpennessWeightedSum;
    }

    public float getAdjustmentWeight() {
        return adjustmentWeight;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isRatedbyOther() {
        return isRatedbyOther;
    }

    public void setRatedbyOther(boolean ratedbyOther) {
        isRatedbyOther = ratedbyOther;
    }

    public boolean isAttended() {
        return isAttended;
    }

    public int getActype() {
        return acType;
    }

    public int getGender() {
        return gender;
    }

    public void setMsgToken(String msgToken) {
        this.msgToken = msgToken;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setProPic(String proPic) {
        this.proPic = proPic;
    }
}
