package com.nconnect.teacher.model.Notifications;

import java.util.List;

public class Result {
    private int total_pages;

    private String response;

    private List<Notifications> notifications;

    private String message;
    private Result result;
    private int unread;


    public void setTotal_pages(int total_pages){
        this.total_pages = total_pages;
    }
    public int getTotal_pages(){
        return this.total_pages;
    }
    public void setResponse(String response){
        this.response = response;
    }
    public String getResponse(){
        return this.response;
    }
    public void setNotifications(List<Notifications> notifications){
        this.notifications = notifications;
    }
    public List<Notifications> getNotifications(){
        return this.notifications;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }
}