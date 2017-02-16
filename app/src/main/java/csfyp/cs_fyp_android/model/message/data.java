package csfyp.cs_fyp_android.model.message;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

/**
 * Created by ray on 16/2/2017.
 */

public class data {
    public final int PHOTO_TYPE =1;
    public final int AUDIO_TYPE =2;

    private String id;
    private String uid;
    private String displayName;
    private String content;
    private int type; //0 is text, 1 is photo, 2 is audio
    private Long creationDate;

    public data(String uid,String displayName, String content) {
        this.uid = uid;
        this.displayName = displayName;
        this.content = content;
    }

    public data(String uid,String displayName, String content, int type){
        this.uid = uid;
        this.displayName = displayName;
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public int getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public java.util.Map<String, String> getCreationDate() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreationDateLong() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

}
