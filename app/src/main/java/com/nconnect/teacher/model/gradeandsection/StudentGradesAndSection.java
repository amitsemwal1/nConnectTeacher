package com.nconnect.teacher.model.gradeandsection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.model.Error;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.Result;

public class StudentGradesAndSection {
    @SerializedName("id")
    @Expose
    private Object id;
    @SerializedName("result")
    @Expose
    private Result result;
    @SerializedName("jsonrpc")
    @Expose
    private String jsonrpc;
    @SerializedName("error")
    @Expose
    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
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

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    @SerializedName("params")
    @Expose
    Params params;

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }
}
