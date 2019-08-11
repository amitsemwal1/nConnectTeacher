package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;

public class Otp {

    @SerializedName(Constants.PARAMS)
    @Expose
    private Params params;

    @SerializedName(Constants.JSON_RPC_)
    @Expose
    private String jsonRpc;

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getJsonRpc() {
        return jsonRpc;
    }

    public void setJsonRpc(String jsonRpc) {
        this.jsonRpc = jsonRpc;
    }

    @SerializedName(Constants.RESULT)
    @Expose
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
