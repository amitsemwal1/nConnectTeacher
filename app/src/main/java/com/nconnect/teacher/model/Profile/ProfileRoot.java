package com.nconnect.teacher.model.Profile;

public class ProfileRoot {
    private String id;

    private String jsonrpc;

    private Result result;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setJsonrpc(String jsonrpc){
        this.jsonrpc = jsonrpc;
    }
    public String getJsonrpc(){
        return this.jsonrpc;
    }
    public void setResult(Result result){
        this.result = result;
    }
    public Result getResult(){
        return this.result;
    }
}