package com.nconnect.teacher.model.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.nconnect.teacher.util.Constants;

public class LoginStatus {
    @SerializedName(Constants.USERNAME)
    @Expose
    private String userName;
    @SerializedName(Constants.STUDENT_ID)
    @Expose
    private String studentId;
    @SerializedName(Constants.PROFILE_IMAGE)
    @Expose
    private String profileImage;
    @SerializedName(Constants.USER_TOKEN)
    @Expose
    private int userToken;
    @SerializedName(Constants.USER_TYPE)
    @Expose
    private String userType;
    @SerializedName(Constants.STUDENT_TOKEN)
    @Expose
    private int studentToken;
    @SerializedName(Constants.OTP)
    @Expose
    private String otp;
    @SerializedName(Constants.STATUS)
    @Expose
    private boolean status;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getUserToken() {
        return userToken;
    }

    public void setUserToken(int userToken) {
        this.userToken = userToken;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getStudentToken() {
        return studentToken;
    }

    public void setStudentToken(int studentToken) {
        this.studentToken = studentToken;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
