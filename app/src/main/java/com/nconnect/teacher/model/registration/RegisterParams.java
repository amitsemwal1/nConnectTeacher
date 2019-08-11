package com.nconnect.teacher.model.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.nconnect.teacher.util.Constants;

public class RegisterParams {

    @SerializedName(Constants.REGISTER_MOBILE_NUMBER)
    @Expose
    private String regMobile;
    @SerializedName(Constants.STUDENT_ID)
    @Expose
    private List<String> studentId = null;
    @SerializedName(Constants.TEACHER_ID)
    @Expose
    private String teacherId;

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public RegisterParams(String regMobile, List<String> studentId) {
        this.regMobile = regMobile;
        this.studentId = studentId;
    }

    public RegisterParams() {

    }

    public String getRegMobile() {
        return regMobile;
    }

    public void setRegMobile(String regMobile) {
        this.regMobile = regMobile;
    }

    public List<String> getStudentId() {
        return studentId;
    }

    public void setStudentId(List<String> studentId) {
        this.studentId = studentId;
    }

}
