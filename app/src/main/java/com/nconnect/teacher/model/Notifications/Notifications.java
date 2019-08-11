package com.nconnect.teacher.model.Notifications;

public class Notifications {
    private int id;

    private Request request;

    private String body;

    private String title;

    private String date;

    private String topic;
    private String value;
    private boolean read;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setRequest(Request request){
        this.request = request;
    }
    public Request getRequest(){
        return this.request;
    }
    public void setBody(String body){
        this.body = body;
    }
    public String getBody(){
        return this.body;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    public void setTopic(String topic){
        this.topic = topic;
    }
    public String getTopic(){
        return this.topic;
    }
    public void setValue(String value){
        this.value = value;
    }
    public String getValue(){
        return this.value;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}