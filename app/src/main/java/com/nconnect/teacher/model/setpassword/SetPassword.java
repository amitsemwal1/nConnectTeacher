package com.nconnect.teacher.model.setpassword;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.util.Constants;

public class SetPassword {
    @SerializedName(Constants.JSON_RPC_)
    @Expose
    private String jsonrpc;
    @SerializedName(Constants.ID)
    @Expose
    private String id;
    @SerializedName(Constants.RESULT)
    @Expose
    private Result result;

    public SetPassword(String jsonrpc, SetPasswordParams setPasswordParams) {
        this.jsonrpc = jsonrpc;
        this.setPasswordParams = setPasswordParams;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
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
    private SetPasswordParams setPasswordParams;

    public SetPasswordParams getRegisterParams() {
        return setPasswordParams;
    }

    public void setRegisterParams(SetPasswordParams registerParams) {
        this.setPasswordParams = registerParams;
    }
}
