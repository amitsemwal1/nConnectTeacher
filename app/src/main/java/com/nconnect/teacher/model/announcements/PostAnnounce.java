package com.nconnect.teacher.model.announcements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.util.Constants;

public class PostAnnounce {
    @SerializedName(Constants.JSON_RPC_)
    @Expose
    private String jsonrpc;

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

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public Integer getResultStr() {

        return resultStr;
    }

    public void setResultStr(Integer resultStr) {
        this.resultStr = resultStr;
    }

    @SerializedName(Constants.METHOD)
    @Expose
    private String method;
    @SerializedName(Constants.PARAMS)
    @Expose
    private Params params;
    @SerializedName(Constants.RESULT)
    @Expose
    private Integer resultStr;

}
