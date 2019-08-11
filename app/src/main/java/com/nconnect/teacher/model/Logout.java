package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.model.login.LoginParam;
import com.nconnect.teacher.util.Constants;

public class Logout {
    @SerializedName(Constants.JSON_RPC_)
    @Expose
    private String jsonrpc;
    @SerializedName(Constants.METHOD)
    @Expose
    private String method;
    @SerializedName(Constants.PARAMS)
    @Expose
    private LoginParam loginParam;
    @Expose
    private Result result;
    @SerializedName(Constants.ID)
    @Expose
    private String id;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LoginParam getLoginParam() {
        return loginParam;
    }

    public void setLoginParam(LoginParam loginParam) {
        this.loginParam = loginParam;
    }
}
