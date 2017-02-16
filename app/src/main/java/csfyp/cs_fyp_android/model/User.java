package csfyp.cs_fyp_android.model;

public class User {
    private int id;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String nickName;
    private boolean isMale;
    private boolean isRatedbyOther;
    private boolean isSelfRated;
    private int attendEventNum;
    private int missingEventNum;
    private int holdingEventNum;
    private String email;
    private String phone;
    private String description;
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
        this.isSelfRated = false;
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
}
