package com.nconnect.teacher.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;

public class Child {
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @SerializedName(Constants.PROFILE_IMAGE_HOME)
    @Expose
    private String profileImage;

    public Integer getStudentToken() {
        return studentToken;
    }

    public void setStudentToken(Integer studentToken) {
        this.studentToken = studentToken;
    }

    @SerializedName(Constants.STUDENT_TOKEN)
    @Expose
    private Integer studentToken;
    @SerializedName(Constants.STUDENT_ID)
    @Expose
    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
