package com.nconnect.teacher.model.chat;

public class Father {
    private String username;

    private boolean installed;

    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setInstalled(boolean installed){
        this.installed = installed;
    }
    public boolean getInstalled(){
        return this.installed;
    }
}