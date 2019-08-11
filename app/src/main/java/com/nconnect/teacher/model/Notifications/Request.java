package com.nconnect.teacher.model.Notifications;

public class Request {
    private int value;

    private String topic;

    private String model;

    public void setValue(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
    public void setTopic(String topic){
        this.topic = topic;
    }
    public String getTopic(){
        return this.topic;
    }
    public void setModel(String model){
        this.model = model;
    }
    public String getModel(){
        return this.model;
    }
}