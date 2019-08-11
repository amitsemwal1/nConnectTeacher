package com.nconnect.teacher.model.Profile;

public class Result {
    private Data data;

    private String message;

    private String response;

    public void setData(Data data){
        this.data = data;
    }
    public Data getData(){
        return this.data;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setResponse(String response){
        this.response = response;
    }
    public String getResponse(){
        return this.response;
    }
}