package com.nconnect.teacher.model.chat;

import java.util.List;

public class Result {
    private String response;

    private List<Parents> parents;

    public void setResponse(String response){
        this.response = response;
    }
    public String getResponse(){
        return this.response;
    }
    public void setParents(List<Parents> parents){
        this.parents = parents;
    }
    public List<Parents> getParents(){
        return this.parents;
    }
}