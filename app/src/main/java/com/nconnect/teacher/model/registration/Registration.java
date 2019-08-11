package com.nconnect.teacher.model.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.util.Constants;

public class Registration {
    @SerializedName(Constants.JSON_RPC_)
    @Expose
    private String jsonrpc;
    @SerializedName(Constants.ID)
    @Expose
    private String id;
    @SerializedName(Constants.RESULT)
    @Expose
    private Result result;

    public Registration(String jsonrpc, RegisterParams registerParams) {
        this.jsonrpc = jsonrpc;
        this.registerParams = registerParams;
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
    private RegisterParams registerParams;

    public RegisterParams getRegisterParams() {
        return registerParams;
    }

    public void setRegisterParams(RegisterParams registerParams) {
        this.registerParams = registerParams;
    }
}
