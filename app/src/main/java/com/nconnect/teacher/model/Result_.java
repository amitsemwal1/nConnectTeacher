package com.nconnect.teacher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result_ {
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("user_token")
    @Expose
    private Integer userToken;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Integer getUserToken() {
        return userToken;
    }

    public void setUserToken(Integer userToken) {
        this.userToken = userToken;
    }

    @SerializedName("mobile_raised_by")
    @Expose
    private String mobileRaisedBy;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("raised_by")
    @Expose
    private String raisedBy;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("issue_id")
    @Expose
    private Integer issueId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("comments")
    @Expose
    private Integer comments;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("assign_to")
    @Expose
    private String assignTo;
    @SerializedName("student")
    @Expose
    private String student;
    @SerializedName("escalate_to")
    @Expose
    private String escalateTo;

    public String getMobileRaisedBy() {
        return mobileRaisedBy;
    }

    public void setMobileRaisedBy(String mobileRaisedBy) {
        this.mobileRaisedBy = mobileRaisedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getEscalateTo() {
        return escalateTo;
    }

    public void setEscalateTo(String escalateTo) {
        this.escalateTo = escalateTo;
    }
}
