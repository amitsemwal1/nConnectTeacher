package com.nconnect.teacher.model.stories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;

public class StoryComment {

    @SerializedName(Constants.CREATED_AT)
    @Expose
    private Integer createdAt;
    @SerializedName(Constants.MESSAGE)
    @Expose
    private String message;
    @SerializedName(Constants.LOGIN_TYPE)
    @Expose
    private String loginType;
    @SerializedName(Constants.MESSAGE_ID)
    @Expose
    private Integer messageId;
    @SerializedName(Constants.NAME)
    @Expose
    private String name;
    @SerializedName(Constants.USERID)
    @Expose
    private Integer userId;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;

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

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
