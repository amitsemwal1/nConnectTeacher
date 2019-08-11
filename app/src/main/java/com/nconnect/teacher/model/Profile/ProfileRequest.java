package com.nconnect.teacher.model.Profile;

import com.google.gson.annotations.SerializedName;

public class ProfileRequest {
    @SerializedName("jsonrpc")
    public String jsonrpc;

    @SerializedName("params")
    public Params params;

    public ProfileRequest(String jsonrpc, Params params) {
        this.jsonrpc = jsonrpc;
        this.params = params;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public static class Params{
    }
}