package com.nconnect.teacher.model.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nconnect.teacher.model.Error;

public class ParentsRoot {
    private String id;

    private String jsonrpc;

    private Result result;

    @SerializedName("error")
    @Expose
    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getJsonrpc() {
        return this.jsonrpc;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }
}