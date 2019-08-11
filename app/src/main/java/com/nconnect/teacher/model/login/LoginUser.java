package com.nconnect.teacher.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.model.Error;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.util.Constants;

public class LoginUser {
    @SerializedName(Constants.JSON_RPC_)
    @Expose
    private String jsonRpc;
    @SerializedName(Constants.RESULT)
    @Expose
    private Result result;
    @SerializedName(Constants.ID)
    @Expose
    private String id;
    @SerializedName(Constants.ERROR)
    @Expose
    private Error error;

    public LoginUser() {

    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public LoginUser(String jsonRpc, LoginParam loginParam) {
        this.jsonRpc = jsonRpc;
        this.loginParam = loginParam;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @SerializedName(Constants.PARAMS)
    @Expose
    private LoginParam loginParam;

    public String getJsonRpc() {
        return jsonRpc;
    }

    public void setJsonRpc(String jsonRpc) {
        this.jsonRpc = jsonRpc;
    }

    public LoginParam getLoginParam() {
        return loginParam;
    }

    public void setLoginParam(LoginParam loginParam) {
        this.loginParam = loginParam;
    }
}
