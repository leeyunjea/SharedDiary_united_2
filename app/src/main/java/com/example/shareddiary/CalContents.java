package com.example.shareddiary;

/**
 * Created by 이예지 on 2017-08-03.
 */

public class CalContents {
    String myName,content, time, location,date;
    public  CalContents(){

    }

    public  CalContents(String date,String time,String content,String location,String myName){
        this.date = date;
        this.time = time;
        this.content = content;
        this.location = location;
        this.myName = myName;
    }

    public String getContent(){ return content;}
    public String getTime(){ return  time;}
    public String getLocation(){ return location;}
    public String getDate(){ return date;}
    public String getMyName(){ return myName;}
}
