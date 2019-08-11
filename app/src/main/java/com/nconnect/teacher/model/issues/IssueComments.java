package com.nconnect.teacher.model.issues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.util.Constants;

public class IssueComments {
    @SerializedName(Constants.JSON_RPC_)
    @Expose
    private String jsonrpc;
    @SerializedName(Constants.RESULT)
    @Expose
    private Result result;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @SerializedName(Constants.ID)
    @Expose
    private Integer id;

    @SerializedName(Constants.PARAMS)
    @Expose
    private Params params;

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

}
