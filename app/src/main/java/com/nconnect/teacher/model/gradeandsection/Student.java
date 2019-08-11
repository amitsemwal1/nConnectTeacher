package com.nconnect.teacher.model.gradeandsection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("student_token")
    @Expose
    private Integer studentToken;
    @SerializedName("student_id")
    @Expose
    private String studentId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_present")
    @Expose
    private Boolean isPresent;

    public Boolean getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Boolean isPresent) {
        this.isPresent = isPresent;
    }

    public Integer getStudentToken() {
        return studentToken;
    }

    public void setStudentToken(Integer studentToken) {
        this.studentToken = studentToken;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
