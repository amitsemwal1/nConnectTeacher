package com.nconnect.teacher.model.stories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.model.Error;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;
import com.nconnect.teacher.util.Constants;

public class Stories {
    @SerializedName(Constants.ID)
    @Expose
    private Object id;
    @SerializedName(Constants.RESULT)
    @Expose
    private Result result;
    @SerializedName(Constants.JSON_RPC_)
    @Expose
    private String jsonrpc;
    @SerializedName(Constants.METHOD)
    @Expose
    private String method;
    @SerializedName("error")
    @Expose
    private Error error;
    @SerializedName("total_pages")
    private String totalPages;

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Params getStoriesParams() {
        return storiesParams;
    }

    public void setStoriesParams(Params storiesParams) {
        this.storiesParams = storiesParams;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    @SerializedName(Constants.PARAMS)
    @Expose
    private Params storiesParams;
}
