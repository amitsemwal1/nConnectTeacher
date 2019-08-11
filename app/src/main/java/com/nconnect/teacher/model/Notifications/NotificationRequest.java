package com.nconnect.teacher.model.Notifications;

import com.google.gson.annotations.SerializedName;

public class NotificationRequest {
    @SerializedName("jsonrpc")
    public String jsonrpc;

    @SerializedName("params")
    public Params params;

    public NotificationRequest(String jsonrpc, Params params) {
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

        @SerializedName("page")
        public int page;

        public Params(int page) {
            this.page = page;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }
}