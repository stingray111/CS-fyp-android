/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package csfyp.cs_fyp_android.chat;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;

import csfyp.cs_fyp_android.MainActivity;

public class FriendlyMessage {

    private String id;
    private String content;
    private String uid;
    private Long creationDate;
    private String displayName;
    private String photoUrl;
    private int type; //0 is text,1 photo, 2 audio

    public FriendlyMessage() {
    }


    public FriendlyMessage(String uid, String displayName,String content) {
        this.uid = uid;
        this.displayName = displayName;
        this.content = content;
        this.type = 0;
    }

    public FriendlyMessage(String uid, String displayName,String content,int type) {
        this.uid = uid;
        this.displayName = displayName;
        this.content = content;
        this.type = type;

    }


    public String getDisplayName() {
        return displayName;
    }

    public int getType() {
        return type;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public java.util.Map<String, String> getCreationDate() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreationDateLong() {
        return creationDate;
    }

    @Exclude
    public Date getDate(){
        return new Date(creationDate);
    }

    @Exclude
    public String getTime(){
        Date date = getDate();
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        return sfd.format(date);
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}

