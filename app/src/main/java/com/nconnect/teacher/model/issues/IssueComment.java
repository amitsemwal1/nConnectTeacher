package com.nconnect.teacher.model.issues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;

public class IssueComment {
    @SerializedName(Constants.CREATED_AT)
    @Expose
    private Integer createdAt;
    @SerializedName(Constants.MESSAGE)
    @Expose
    private String message;
    @SerializedName(Constants.NAME)
    @Expose
    private String name;
    @SerializedName(Constants.USERID)
    @Expose
    private Integer userId;
    @SerializedName(Constants.LOGIN_TYPE)
    @Expose
    private String login_type;

    @SerializedName("profile_image")
    @Expose
    private String profileImage;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    @SerializedName(Constants.MESSAGE_ID)
    @Expose
    private Integer messageId;
}
