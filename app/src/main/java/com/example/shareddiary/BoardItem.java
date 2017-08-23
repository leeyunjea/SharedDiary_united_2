package com.example.shareddiary;

import android.content.Context;

/**
 * Created by HANEUL on 2017-07-29.
 */

public class BoardItem {
    String title;
    String userID;
    String date;
    String heart; //다시
    int profile;
    String contents;
    
    public BoardItem() {}

    public BoardItem(String title, String userID, String date, String heart, int profile , String contents) {
        this.title = title;
        this.userID = userID;
        this.date = date;
        this.heart = heart;
        this.profile = profile;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getHeart() {
        return heart;
    }
    public void setHeart(String heart) {
        this.heart = heart;
    }

    public int getProfile() {
        return profile;
    }
    public void setProfile(int profile) {
        this.profile = profile;
    }
}