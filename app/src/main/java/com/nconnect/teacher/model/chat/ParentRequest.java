package com.nconnect.teacher.model.chat;

import com.google.gson.annotations.SerializedName;

public class ParentRequest {
    @SerializedName("jsonrpc")
    public String jsonrpc;

    @SerializedName("params")
    public Params params;

    public ParentRequest(String jsonrpc, Params params) {
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

    public static class Params {

        @SerializedName("grade_id")
        public int grade_id;

        @SerializedName("section_id")
        public int section_id;

        @SerializedName("page")
        private Integer page;

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Params(int grade_id, int section_id, int page) {
            this.grade_id = grade_id;
            this.section_id = section_id;
            this.page = page;
        }

        public int getGrade_id() {
            return grade_id;
        }

        public void setGrade_id(int grade_id) {
            this.grade_id = grade_id;
        }

        public int getSection_id() {
            return section_id;
        }

        public void setSection_id(int section_id) {
            this.section_id = section_id;
        }
    }
}