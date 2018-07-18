package com.example.rahulyiet.instagramclone;

import android.widget.ImageView;

public class PostModel {

    String userName,userImgUrl,userEmail,postImgUrl,postTime;
    ImageView img1;

    public PostModel(String userName, String userImgUrl, String userEmail, String postImgUrl, String postTime) {
        this.userName = userName;
        this.userImgUrl = userImgUrl;
        this.userEmail = userEmail;
        this.postImgUrl = postImgUrl;
        this.postTime = postTime;
    }




     public PostModel(){


     }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String usernName) {
        this.userName = usernName;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPostImgUrl() {
        return postImgUrl;
    }

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}
