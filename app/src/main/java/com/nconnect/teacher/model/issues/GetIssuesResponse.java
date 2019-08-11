package com.nconnect.teacher.model.issues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.nconnect.teacher.model.Error;

public class GetIssuesResponse {

    @SerializedName("error")
    @Expose
    private Error error;

    @SerializedName("result")
    private Result result;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        @SerializedName("response")
        private String response;

        @SerializedName("total_pages")
        private int totalPages;

        @SerializedName("page")
        private int page;

        @SerializedName("issues")
        private List<IssueDetails> issues;

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<IssueDetails> getIssues() {
            return issues;
        }

        public void setIssues(List<IssueDetails> issues) {
            this.issues = issues;
        }
    }

    public class IssueDetails {
        @SerializedName("issue_id")
        @Expose
        private Integer issueId;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("description")
        @Expose
        private String description;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("comments")
        @Expose
        private Integer comments;

        @SerializedName("raised_by")
        @Expose
        private String raisedBy;

        @SerializedName("student")
        @Expose
        private String student;

        @SerializedName("mobile_raised_by")
        @Expose
        private String mobileRaisedBy;

        public Integer getIssueId() {
            return issueId;
        }

        public void setIssueId(Integer issueId) {
            this.issueId = issueId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getComments() {
            return comments;
        }

        public void setComments(Integer comments) {
            this.comments = comments;
        }

        public String getRaisedBy() {
            return raisedBy;
        }

        public void setRaisedBy(String raisedBy) {
            this.raisedBy = raisedBy;
        }

        public String getStudent() {
            return student;
        }

        public void setStudent(String student) {
            this.student = student;
        }

        public String getMobileRaisedBy() {
            return mobileRaisedBy;
        }

        public void setMobileRaisedBy(String mobileRaisedBy) {
            this.mobileRaisedBy = mobileRaisedBy;
        }
    }
}

